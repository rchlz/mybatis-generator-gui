package com.ddhigh.mybatis.plugins;

import org.apache.log4j.Logger;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

/**
 * Created by guoweiwei on 2016/10/10.
 */
public class QueryAllGeneratorPlugin extends BaseGeneratorPlugin {

    private static Logger logger = Logger.getLogger(QueryAllGeneratorPlugin.class);

    /**
     * {@inheritDoc}
     */
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean clientGenerated(Interface interfaze,
                                   TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {

        interfaze.addImportedType(FullyQualifiedJavaType.getNewListInstance());
        interfaze.addImportedType(new FullyQualifiedJavaType("com.github.miemiedev.mybatis.paginator.domain.PageBounds"));
        interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Param"));
        interfaze.addMethod(generateQueryAllMethod(introspectedTable));

        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {

        logger.debug("queryAll generator for table" + introspectedTable.getTableConfiguration().getTableName());

        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();//数据库表名

        XmlElement parentElement = document.getRootElement();

        // 产生分页语句前半部分
        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute("id", "queryAll"));
        answer.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));

        answer.addElement(new TextElement("select"));
        answer.addElement(getBaseColumnListElement(introspectedTable));
        answer.addElement(new TextElement("from " + tableName));

        parentElement.addElement(answer);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    private Method generateQueryAllMethod(IntrospectedTable introspectedTable) {

        Method method = new Method("queryAll");
        method.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        returnType.addTypeArgument(listType);
        method.setReturnType(returnType);

        //m.addParameter(new Parameter(FullyQualifiedJavaType.getIntInstance(), "deleteFlag", "@Param(\"deleteFlag\")"));
        //m.addParameter(new Parameter(new FullyQualifiedJavaType("Integer[]"), "ids", "@Param(\"ids\")"));

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);
        return method;
    }

}
