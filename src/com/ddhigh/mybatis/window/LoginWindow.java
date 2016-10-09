package com.ddhigh.mybatis.window;

import com.ddhigh.mybatis.entity.TableEntity;
import com.ddhigh.mybatis.util.DbUtil;
import com.ddhigh.mybatis.util.GUIUtil;
import com.ddhigh.mybatis.worker.GetTablesWorker;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class LoginWindow {
    private JPanel container;
    private JButton btnConnect;
    private JComboBox comboBoxType;
    private JTextField txtHost;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtPort;
    private JTextField txtDatabase;
    private JButton btnSave;
    private static Logger logger = Logger.getLogger(LoginWindow.class);
    private static JFrame frame;

    FileReader fileReader = null;
    Properties properties = new Properties();

    public LoginWindow() {

        initLoginData();

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                btnSave.setText("保存中...");
                btnSave.setEnabled(false);

                String host = txtHost.getText().trim();
                String port = txtPort.getText().trim();
                String username = txtUsername.getText().trim();
                String password = txtPassword.getText().trim();
                int type = comboBoxType.getSelectedIndex();
                String database = txtDatabase.getText().trim();

                properties.setProperty("host",host);
                properties.setProperty("port",port);
                properties.setProperty("username",username);
                properties.setProperty("password",password);
                properties.setProperty("type",String.valueOf(type));
                properties.setProperty("database",database);

                try {
                    FileWriter fileWriter = new FileWriter("config.ini");
                    properties.store(fileWriter, null);
                    JOptionPane.showMessageDialog(frame,"保存成功！");
                }catch (IOException e1){
                    logger.error("save config file error!", e1);
                    JOptionPane.showMessageDialog(frame,"保存失败！");
                }

                btnSave.setText("保存");
                btnSave.setEnabled(true);

            }
        });

        btnConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String host = txtHost.getText().trim();
                String port = txtPort.getText().trim();
                String username = txtUsername.getText().trim();
                String password = txtPassword.getText().trim();
                String type = comboBoxType.getSelectedItem().toString();
                String database = txtDatabase.getText().trim();
                if (host.isEmpty() || port.isEmpty() || username.isEmpty() || database.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "请填写必填信息");
                    return;
                }
                connect(host, port, username, password, type, database);
            }
        });
        comboBoxType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selected = comboBoxType.getSelectedIndex();
                switch (selected) {
                    case 0:
                        txtPort.setText(String.valueOf(DbUtil.portMap.get(DbUtil.Type.MySQL)));
                        break;
                    case 1:
                        txtPort.setText(String.valueOf(DbUtil.portMap.get(DbUtil.Type.Oracle)));
                        break;
                }
            }
        });
    }

    private void initLoginData(){

        try {
            File configFile = new File("config.ini");
            if (!configFile.exists() || configFile.isFile()) {
                configFile.createNewFile();
            }
            properties.load(new FileReader(configFile));
        }catch (IOException e){
            logger.error("read config file error", e);
        }

        properties.getProperty("host","localhost");

        String host = properties.getProperty("host","localhost");
        String port = properties.getProperty("port","3306");
        String username = properties.getProperty("username","root");
        String password = properties.getProperty("password","");
        String typeStr = properties.getProperty("type","0");
        String database = properties.getProperty("database","");

        txtHost.setText(host);
        txtPort.setText(port);
        txtUsername.setText(username);
        txtPassword.setText(password);
        txtDatabase.setText(database);
        int type = Integer.valueOf(typeStr);
        comboBoxType.setSelectedIndex(type);
    }

    private void connect(final String host, final String port, final String username, final String password, final String type, final String database) {
        btnConnect.setText("连接中...");
        btnConnect.setEnabled(false);
        GetTablesWorker getTablesWorker = new GetTablesWorker(host, port, username, password, type, database);
        getTablesWorker.setListener(new GetTablesWorker.OnLoadedListener() {
            @Override
            public void onSuccess(List<TableEntity> list) {
                DbUtil.Type dbType;
                switch (type) {
                    case "Oracle":
                        dbType = DbUtil.Type.Oracle;
                        break;
                    default:
                        dbType = DbUtil.Type.MySQL;
                        break;
                }
                new Dashboard(new DbUtil(host, port, username, password, dbType, database));
                frame.dispose();
            }

            @Override
            public void onError(String message, Throwable ex) {
                btnConnect.setEnabled(true);
                btnConnect.setText("连接");
                JOptionPane.showMessageDialog(frame, message);
                logger.error(message, ex);
            }
        });
        getTablesWorker.execute();
    }

    public static void main(String[] args) {
        GUIUtil.setFont(new Font("Microsoft Yahei", Font.PLAIN, 12));
        try {
            UIManager.setLookAndFeel(GUIUtil.LOOK_AND_FEEL_WINDOWS);
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
        frame = new JFrame("连接数据库");
        frame.setContentPane(new LoginWindow().container);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        GUIUtil.setCenter(frame);
        frame.setVisible(true);
    }
}
