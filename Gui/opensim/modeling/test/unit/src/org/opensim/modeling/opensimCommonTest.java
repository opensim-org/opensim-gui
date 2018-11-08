/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.modeling;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author chris
 */
public class opensimCommonTest {
    
    public opensimCommonTest() {
    }

    /**
     * Test of GetVersion method, of class opensimCommon.
     */
    @Test
    public void testGetVersion() {
        System.out.println("GetVersion");
        String expResult = "4.0";
        String result = opensimCommon.GetVersion();
        assertEquals(expResult, result);
    }
}
