package com.ddhigh.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.PluginAdapter;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;

/**
 * Created by guoweiwei on 2016/10/10.
 */
public class RenamePrimaryKeyPlugin extends PluginAdapter {
    private String searchString;
    private String replaceString;
    private Pattern pattern;

    /**
     *
     */
    public RenamePrimaryKeyPlugin() {
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        introspectedTable.setDeleteByPrimaryKeyStatementId("delete");
        introspectedTable.setSelectByPrimaryKeyStatementId("select");
        introspectedTable.setUpdateByPrimaryKeyStatementId("update");
        introspectedTable.setUpdateByPrimaryKeySelectiveStatementId("updateSelective");
    }

    public static String firstLetterToUpperCase(String str){

        if(str == null || str.length() <= 0){
            return "";
        }

        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        str = sb.toString();
        return str;
    }

    public static String generatorPrimaryKeySuffix(IntrospectedTable introspectedTable){
        List<IntrospectedColumn> columns = introspectedTable.getPrimaryKeyColumns();

        String suffix = "";
        int i = 0;

        for (IntrospectedColumn column : columns){
            if (i > 0) {
                suffix += "And" + column.getJavaProperty();
            }else{
                suffix += column.getJavaProperty();
            }
            i++;
        }

        return suffix;
    }

}

