/*
 * SVG Salamander
 * Copyright (c) 2004, Mark McKay
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 *   - Redistributions of source code must retain the above 
 *     copyright notice, this list of conditions and the following
 *     disclaimer.
 *   - Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials 
 *     provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Mark McKay can be contacted at mark@kitfox.com.  Salamander and other
 * projects can be found at http://www.kitfox.com
 *
 * Created on January 13, 2005, 7:23 AM
 */

package com.kitfox.svg.app;

import com.kitfox.svg.SVGConst;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.text.html.*;


/**
 *
 * @author  kitfox
 */
public class VersionDialog extends javax.swing.JDialog
{
    public static final long serialVersionUID = 1;
    
    final boolean verbose;
    
    /** Creates new form VersionDialog */
    public VersionDialog(java.awt.Frame parent, boolean modal, boolean verbose)
    {
        super(parent, modal);
        initComponents();
        
        this.verbose = verbose;
        
        textpane_text.setContentType("text/html");
        
        StringBuffer sb = new StringBuffer();
        try
        {
            URL url = getClass().getResource("/res/help/about/about.html");
            if (verbose)
            {
                System.err.println("" + getClass() + " trying to load about html " + url);
            }
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            while (true)
            {
                String line = reader.readLine();
                if (line == null) break;
                sb.append(line);
            }
            
            textpane_text.setText(sb.toString());
        }
        catch (Exception e)
        {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING, null, e);
        }
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        jPanel1 = new javax.swing.JPanel();
        textpane_text = new javax.swing.JTextPane();
        jPanel2 = new javax.swing.JPanel();
        bn_close = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About SVG Salamander");
        jPanel1.setLayout(new java.awt.BorderLayout());

        textpane_text.setEditable(false);
        textpane_text.setPreferredSize(new java.awt.Dimension(400, 300));
        jPanel1.add(textpane_text, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        bn_close.setText("Close");
        bn_close.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bn_closeActionPerformed(evt);
            }
        });

        jPanel2.add(bn_close);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void bn_closeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bn_closeActionPerformed
    {//GEN-HEADEREND:event_bn_closeActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_bn_closeActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                new VersionDialog(new javax.swing.JFrame(), true, true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bn_close;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextPane textpane_text;
    // End of variables declaration//GEN-END:variables
    
}
