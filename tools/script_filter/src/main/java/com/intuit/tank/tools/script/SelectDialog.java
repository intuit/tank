/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.tools.script;

/*
 * #%L
 * script-filter
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.apache.commons.lang3.StringUtils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

/**
 * ScriptSelectDialog
 * 
 * @author dangleton
 * 
 */
public class SelectDialog<SELECTION_TYPE extends Object> extends JDialog {

    private static final long serialVersionUID = 1L;

    private JTextField filterField;
    private JList list;
    private JButton okBT;
    private SELECTION_TYPE selectedObject;
    private List<SELECTION_TYPE> externalScripts;
    private long timeClicked;

    /**
     * @param f
     * @param externalScripts
     */
    public SelectDialog(Frame f, List<SELECTION_TYPE> externalScripts) {
        super(f, true);
        this.externalScripts = externalScripts;
        setLayout(new BorderLayout());
        filterField = new JTextField();
        filterField.addKeyListener(new KeyHandler());
        list = new JList(externalScripts.toArray());
        list.addListSelectionListener( (ListSelectionEvent e) -> okBT.setEnabled(list.getSelectedIndex() != -1));
        list.addMouseListener(new MouseAdapter() {

            /**
             * @inheritDoc
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    select();
                }
            }

        });
        add(new JLabel("Select a script."), BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(list);
        add(filterField, BorderLayout.NORTH);
        add(sp, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        setSize(new Dimension(400, 500));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());
        WindowUtil.centerOnParent(this);
    }

    /**
     * @return the selectedScript
     */
    public SELECTION_TYPE getSelectedObject() {
        return selectedObject;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setVisible(boolean b) {
        if (b) {
            selectedObject = null;
        }
        super.setVisible(b);
    }

    /**
     * @return
     */
    private Component createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 5));
        JButton cancelBT = new JButton("Cancel");
        cancelBT.addActionListener( (ActionEvent arg0) -> setVisible(false));
        okBT = new JButton("Ok");
        okBT.addActionListener( (ActionEvent arg0) -> select());

        panel.add(okBT);
        panel.add(cancelBT);
        return panel;
    }

    /**
     * 
     */
    private void select() {
        selectedObject = (SELECTION_TYPE) list.getSelectedValue();
        if (selectedObject != null) {
            setVisible(false);
        }
    }

    public void filter(final long timeValue) {
        new Thread( () -> {
            try {
                Thread.sleep(200);
                if (timeValue == timeClicked) {
                    SwingUtilities.invokeLater( () -> {
                        list.setListData(externalScripts.stream().filter(obj -> StringUtils.isBlank(filterField.getText())
                                || StringUtils.containsIgnoreCase(obj.toString(), filterField.getText()
                                .trim())).toArray());
                        list.repaint();
                    });
                } else {
                    System.out.println("skipping...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    class KeyHandler extends KeyAdapter {

        public void keyPressed(KeyEvent evt) {
            if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                setVisible(false);
            }
        }

        @Override
        public void keyTyped(KeyEvent arg0) {
            timeClicked = System.currentTimeMillis();
            filter(timeClicked);
        }
    }

}
