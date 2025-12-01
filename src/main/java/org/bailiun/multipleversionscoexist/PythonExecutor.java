package org.bailiun.multipleversionscoexist;


import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * &#064;作者:  bailiun
 * &#064;版本:  2.0.0
 * &#064;功能:  Python脚本操作器
 */
public class PythonExecutor {
    //最后输出的值
    private static final List<String> endValues = new ArrayList<>();

    /**
     * &#064;作者:  bailiun
     * &#064;版本:  1.0.0
     * &#064;功能:  运行Python脚本（不包含需要中途操作的脚本）
     */
    public static List<String> run_python_static(String pyPath, String value) {
        String args1 = "python ".concat(pyPath).concat(" ").concat(value);
        ArrayList<String> message;
        try {
            Process process = Runtime.getRuntime().exec(args1);
            message = getMessage(process.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return message;
    }

    /**
     * &#064;作者:  bailiun
     * &#064;版本:  1.0.0
     * &#064;功能:  获取运行Python脚本后的返回数据
     */
    private static ArrayList<String> getMessage(final InputStream input) throws UnsupportedEncodingException {
        ArrayList<String> qwe = new ArrayList<>();
        Reader reader;
        reader = new InputStreamReader(input, "GBK");
        BufferedReader bf = new BufferedReader(reader);
        String line;
        try {
            while ((line = bf.readLine()) != null) {
                qwe.add(line);
            }
            reader.close();
            bf.close();
        } catch (IOException e) {
            System.out.println("脚本执行完成后出现异常");
        }
        return qwe;
    }

    /**
     * &#064;作者:  bailiun
     * &#064;版本:  1.0.0
     * &#064;功能:  实时运行Python脚本
     */
    public static List<String> run_python_dynamic(String pyPath, String value) {
        int exitBalue;
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"python", pyPath, value});
            InputStream in = process.getInputStream();
            OutputStream out = process.getOutputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            int charNum;
            StringBuilder sb = new StringBuilder();
            while ((charNum = bufferedReader.read()) != -1) {
                sb.append((char) charNum);
                endValues.add(String.valueOf((char) charNum));
                if (charNum == '\n' || charNum == '\r') {
                    sb = new StringBuilder();
                }
                if (sb.toString().endsWith("[Y/n] ") ||
                        sb.toString().endsWith("[Y/N] ") ||
                        sb.toString().endsWith("[y/n] ") ||
                        sb.toString().endsWith("[y/N] ")) {
                    Scanner sc = new Scanner(System.in);
                    String str = sc.next();
                    out.write((str + "\n").getBytes(Charset.defaultCharset()));
                    out.flush();
                }
            }
            process.waitFor();
            exitBalue = process.exitValue();
        } catch (IOException | InterruptedException e) {
            exitBalue = 2;
        }
        System.out.println(exitBalue);
        return endValues;
    }

    /**
     * &#064;作者:  bailiun
     * &#064;版本:  1.0.0
     * &#064;功能:  获取纯字符串的JSON内的某个集合的数据
     */
    public static String getMap_MapElement(String data, String plan) {
        char[] arr = data.toCharArray();
        char[] temp = plan.toCharArray();  // data1
        if (!data.contains(plan)) {
            return "不存在字符:" + plan;
        }
        int num = 0;
        int left = 0;
        char te = '\\';
        boolean flat;
        while (true) {
            if (arr[left] == '{' && arr[left] == '[') {
                num++;
                continue;
            }
            if (arr[left] == '}' && arr[left] == ']') {
                num--;
                continue;
            }
            if (arr[left] == temp[0]) {
                flat = true;
                for (int i = 1; i < temp.length; i++) {
                    if (!(arr[left + i] == temp[i])) {
                        flat = false;
                        break;
                    }
                }
                if (flat && num == 0) {
                    for (int i = left; i < arr.length; i++) {
                        if (arr[i] == '{' | arr[i] == '[') {
                            if (flat) {
                                te = (char) ((int) (arr[i]) + 2);
                                flat = false;
                            }
                            num++;
                            continue;
                        }
                        if (arr[i] == '}' | arr[i] == ']')
                            num--;
                        if (arr[i] == te && num == 0)
                            return data.substring(left - 1, i + 1);
                    }
                    return "当前层级不存在此名称的集合";
                }
            }
            left++;
            if (left >= arr.length) {
                return "当前层级不存在此名称的集合";
            }
        }
    }
}
