package com.kdx.vms.commons.util;

import org.junit.Test;

import java.io.FileInputStream;

import static org.junit.Assert.*;

public class Md5UtilTest {

    @Test
    public void testMd5() throws Exception {
    }

    @Test
    public void testMd5ByInputStream() throws Exception {
        FileInputStream in = new FileInputStream(System.getProperty("java.home") + "/lib/rt.jar");
        System.out.println(Md5Util.md5ByInputStream(in));
    }
}