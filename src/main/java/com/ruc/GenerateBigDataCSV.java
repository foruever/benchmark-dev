package com.ruc;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;


public class GenerateBigDataCSV {
    private static final String LINE_SEPARATOR = ",";


    // To be configure
    private static int deviceCount = 3;
    // s0:broken line
    // s1:line
    // s2:square wave with frequency noise
    // s3:long for sin with glitch
    // s4:log
    /**
     * @锯齿波<br>
     * brokenLineConfigs[][0]:起点;<br>
     * brokenLineConfigs[][1]:斜率;<br>
     * brokenLineConfigs[][2]:周期;<br>
     */
    private static int[][] brokenLineConfigs = {{1000, 1, 100}, {200000, 5, 200000},
            {10000, 2, 50000}};
    /**
     * @单调上升直线<br>
     * lineConfigs[][0]:起点<br>
     * lineConfigs[][1]:斜率<br>
     */
    private static long[][] lineConfigs = {{1L << 32, 1}, {1L << 22, 4}, {10000, 2}};
    /**
     * @方波（含异常点） <br>
     * 下界限
     */
    private static float[] squareAmplitude = {12.5f, 1273.143f, 1823767.4f};
    /**
     * @方波（含异常点） <br>
     * 振幅    =上界-下界
     */
    private static float[] squareBaseLine = {25f, 2273.143f, 2823767.4f};
    /**
     * @方波（含异常点） <br>
     * 半个周期
     */
    private static int[] squareLength = {300, 5000, 20000};
    /**
     * @方波（含异常点） <br>
     * 异常点配置
     */
    private static double[][] sinAbnormalConfigs = {{0.28, 20}, {0.3, 100}, {0.35, 50}};
    // y = A*sin(wt), sinConfigs:w,A
    /**
     * @正弦曲线（含噪声点）<br> 配置
     */
    private static double[][] sinConfigs = {{0.05, 2000}, {0.03, 1000}, {0.001, 200}};
    private static int[][] maxMinVertical = {{5000, 4000}, {1000, 800}, {100, 70}};
    private static int[][] maxMinHorizontal = {{5, 2}, {20, 10}, {50, 30}};
    /**
     * @正弦曲线（含噪声点）<br> 噪点几率
     */
    private static double[] glitchProbability = {0.008, 0.01, 0.005};

    // y = A*log(wt), logConfigs:w,A
    private static String deviceType = "root.laptop";
    
    private static float freqWave[] = {0, 0, 0};

    private static void getNextRecordToFile(long timestamp, long index, FileWriter fw) throws IOException {
        for (int i = 0; i < deviceCount; i++) {
            StringBuilder sc = new StringBuilder();
            sc.append("d").append(i);
            sc.append(LINE_SEPARATOR);
            sc.append(timestamp + index);
            sc.append(LINE_SEPARATOR);
            sc.append(deviceType);
            sc.append(LINE_SEPARATOR);
            if (sensorSet.contains("s0")) {
                // s0:broken line, int
                if ((index % brokenLineConfigs[i][2]) == 0)
                    brokenLineConfigs[i][1] = -brokenLineConfigs[i][1];
                brokenLineConfigs[i][0] += brokenLineConfigs[i][1];
                if (brokenLineConfigs[i][0] < 0) {
                    brokenLineConfigs[i][0] = -brokenLineConfigs[i][0];
                    brokenLineConfigs[i][1] = -brokenLineConfigs[i][1];
                }
                sc.append("s0");
                sc.append(LINE_SEPARATOR);
                sc.append(brokenLineConfigs[i][0]);
                sc.append(LINE_SEPARATOR);
            }
            if (sensorSet.contains("s1")) {
                // s1:line, long
                lineConfigs[i][0] += lineConfigs[i][1];
                if (lineConfigs[i][0] < 0)
                    lineConfigs[i][0] = 0;
                sc.append("s1");
                sc.append(LINE_SEPARATOR);
                sc.append(lineConfigs[i][0]);
                sc.append(LINE_SEPARATOR);
            }
            if (sensorSet.contains("s2")) {
                // s2:square wave, float
                if ((index % squareLength[i]) == 0) {
                    squareAmplitude[i] = -squareAmplitude[i];
                    if (hasWrittenFreq[i] == 0) {
                        if ((double) index == squareLength[i]) {
                            System.out.println("d" + i + ":time:" + index + ",sin sin");
                            hasWrittenFreq[i] = 1;
                        }
                    } else if (hasWrittenFreq[i] == 1) {
                        hasWrittenFreq[i] = 2;
                    }
                }
                freqWave[i] =
                        (hasWrittenFreq[i] == 1) ? (float) (squareAmplitude[i] / 2 * Math
                                .sin(sinAbnormalConfigs[i][0] * 2 * Math.PI * index)) : 0;
                sc.append("s2");
                sc.append(LINE_SEPARATOR);
                sc.append(freqWave[i] + squareBaseLine[i] + squareAmplitude[i]);
                sc.append(LINE_SEPARATOR);
            }
            if (sensorSet.contains("s3")) {
                // s3:sin, long
                sc.append("s3");
                sc.append(LINE_SEPARATOR);
                sc.append(generateSinGlitch(timestamp + index, i));
            }
            fw.write(sc.toString() + "\r\n");
        }
    }

    private static Random r = new Random();
    private static int[] width = {-1, -1, -1};
    private static int[] mid = {0, 0, 0};
    private static long[] upPeek = {0, 0, 0};
    private static long[] downPeek = {0, 0, 0};
    private static long[] base = {0, 0, 0};
    private static long[] startAbTime = {0, 0, 0};

    private static long generateSinGlitch(long t, int i) {
        if (r.nextDouble() < glitchProbability[i] && width[i] == -1) {
            startAbTime[i] = t;
            base[i] =
                    (long) (maxMinVertical[i][0] + sinConfigs[i][1] + sinConfigs[i][1]
                            * Math.sin(sinConfigs[i][0] * t));
            width[i] =
                    r.nextInt(maxMinHorizontal[i][0] - maxMinHorizontal[i][1])
                            + maxMinHorizontal[i][1];

            if (width[i] < 2)
                width[i] = 2;
            mid[i] = r.nextInt(width[i] - 1) + 1;
            upPeek[i] =
                    maxMinVertical[i][1] + r.nextInt(maxMinVertical[i][0] - maxMinVertical[i][1]);
            downPeek[i] =
                    maxMinVertical[i][1] + r.nextInt(maxMinVertical[i][0] - maxMinVertical[i][1]);
            return base[i];
        } else {
            if (width[i] != -1) {
                long value;
                // up
                if (t - startAbTime[i] <= mid[i]) {
                    value = (long) (base[i] + ((double) t - startAbTime[i]) / mid[i] * upPeek[i]);
                } else {
                    value =
                            (long) (base[i] + upPeek[i] - ((double) t - mid[i] - startAbTime[i])
                                    / (width[i] - mid[i]) * downPeek[i]);
                }
                if (t - startAbTime[i] == width[i])
                    width[i] = -1;
                // down
                return value;
            } else {
                return (long) (maxMinVertical[i][0] + sinConfigs[i][1] + sinConfigs[i][1]
                        * Math.sin(sinConfigs[i][0] * t));
            }
        }
    }

    private static JSONObject generateTestData(String schemaOutputFilePath) throws IOException {
        JSONArray fileArray = new JSONArray();
        JSONArray rowGroupArray = new JSONArray();
        JSONArray columnGroup1 = new JSONArray();

        String defaultSeriesEncoder = "RLE";
        if (sensorSet.contains("s0")) {
        	JSONObject s1 = new JSONObject();
            s1.put(JsonFormatConstant.SENSOR_ID, "s0");
            s1.put(JsonFormatConstant.SENSOR_TYPE, TSDataType.INT32.toString());
            s1.put(JsonFormatConstant.SENSOR_ENCODING,
                    defaultSeriesEncoder);
            columnGroup1.put(s1);
        }
        if (sensorSet.contains("s1")) {
            JSONObject s2 = new JSONObject();
            s2.put(JsonFormatConstant.SENSOR_ID, "s1");
            s2.put(JsonFormatConstant.SENSOR_TYPE, TSDataType.INT64.toString());
            s2.put(JsonFormatConstant.SENSOR_ENCODING,
                    defaultSeriesEncoder);
            columnGroup1.put(s2);
        }
        if (sensorSet.contains("s2")) {
            JSONObject s3 = new JSONObject();
            s3.put(JsonFormatConstant.SENSOR_ID, "s2");
            s3.put(JsonFormatConstant.SENSOR_TYPE, TSDataType.FLOAT.toString());
            s3.put(JsonFormatConstant.SENSOR_ENCODING,
                    defaultSeriesEncoder);
            s3.put(JsonFormatConstant.FREQUENCY_ENCODING, TSEncoding.DFT);
            s3.put(JsonFormatConstant.DFT_PACK_LENGTH, 300);
            s3.put(JsonFormatConstant.DFT_RATE, 0.4);
            s3.put(JsonFormatConstant.DFT_WRITE_Main_FREQ, true);
            s3.put(JsonFormatConstant.DFT_WRITE_ENCODING, false);
            columnGroup1.put(s3);
        }
        if (sensorSet.contains("s3")) {
            JSONObject s4 = new JSONObject();
            s4.put(JsonFormatConstant.SENSOR_ID, "s3");
            s4.put(JsonFormatConstant.SENSOR_TYPE, TSDataType.INT64.toString());
            s4.put(JsonFormatConstant.SENSOR_ENCODING,
                    defaultSeriesEncoder);
            columnGroup1.put(s4);
        }

        rowGroupArray.put(columnGroup1);

        JSONObject rowGroup = new JSONObject();
        rowGroup.put(JsonFormatConstant.DEV_TYPE, deviceType);
        rowGroup.put(JsonFormatConstant.JSON_GROUPS, rowGroupArray);
        fileArray.put(rowGroup);

        JSONObject jsonSchema = new JSONObject();
        jsonSchema.put(JsonFormatConstant.JSON_SCHEMA, fileArray);
        File file = new File(schemaOutputFilePath);
        FileWriter fw = new FileWriter(file);
        fw.write(jsonSchema.toString());
        fw.close();
        return jsonSchema;
    }

    private static Set<String> sensorSet = new HashSet<>();
    // 0:not write->1:writing->2:written
    private static int hasWrittenFreq[] = {0, 0, 0};

    public static void main(String[] args) throws IOException, InterruptedException {
        args =
                "D://20001w.csv D:freq_specialSchema.json 20000 s0 s1 s2 s3"
                        .split(" ");
        if (args.length < 4) {
            System.err.println("sensorName:s0:int,s1:long,s2:float,s3:long sin");
            System.err
                    .println("input format: <csvFilePath> <schemaOutputFilePath> <lineCount> <sensorName> [<sensorName>...]");
            return;
        }
        System.out.println("write start!");
        String inputDataFile = args[0];
        String schemaOutputFilePath = args[1];
        long lineCount = Long.valueOf(args[2]);
        sensorSet.addAll(Arrays.asList(args).subList(3, args.length));
        generateTestData(schemaOutputFilePath);
        int i = 0;
        File file = new File(inputDataFile);
        if (file.exists())
            file.delete();
        FileWriter fw = new FileWriter(file);
        deviceCount = 3;

        long start = System.currentTimeMillis();
        while (i < lineCount) {
            if (i % 1000000 == 0) {
                System.out.println("generate line count:"+ i);
            }
            getNextRecordToFile(start, i, fw);
            i++;
        }
        fw.close();
        System.out.println("write finished!");
    }

    private class JsonFormatConstant {
        private static final String JSON_SCHEMA = "schema";
        private static final String JSON_GROUPS = "groups";
        private static final String DEV_TYPE = "dev_type";
        private static final String SENSOR_ID = "sensor_id";
        private static final String SENSOR_TYPE = "type";
        private static final String SENSOR_ENCODING = "encoding";
        private static final String FREQUENCY_ENCODING = "freq_encoding";
        private static final String DFT_PACK_LENGTH = "dft_pack_length";
        private static final String DFT_RATE = "dft_rate";
        private static final String DFT_WRITE_Main_FREQ = "write_main_freq";
        private static final String DFT_WRITE_ENCODING = "write_encoding";
    }

    private enum TSEncoding {
        PLAIN, RLE, DIFF, TS_2DIFF, BITMAP, PLA, SDT, DFT
    }

    private enum TSDataType {
        BOOLEAN, INT32, INT64, FLOAT, DOUBLE, BYTE_ARRAY, ENUMS, BIGDECIMAL
    }

}
