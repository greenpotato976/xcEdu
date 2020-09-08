package com.xuecheng.manage_cms;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

/**
 * @author 1159588554@qq.com
 * @date 2020/5/31 13:06
 */
public class Main {
    public static void main(String[] args){
        //公式n方-n除2
        Scanner scanner = new Scanner(System.in);
        int a = scanner.nextInt();
        int[] num = new int[a];
        int[] result = new int[a];
        //初始化数组
        for (int i = 0; i < num.length; i++) {
            num[i] = scanner.nextInt();
            result[i] = (num[i]*num[i]-num[i])/2;
        }
        for (int i = 0; i < result.length; i++) {
            System.out.println(result[i]);
        }
    }
    @Test
    public void test(){
        File file = new File("F:/teach/xcEdu/xcEduUI3940/xc-ui-pc-static-portal/course/detail/test005");
        file.mkdirs();
    }
}
