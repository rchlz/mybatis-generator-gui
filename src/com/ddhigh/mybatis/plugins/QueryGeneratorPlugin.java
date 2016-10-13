package com.ddhigh.mybatis.plugins;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.List;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

/**
 * Created by guoweiwei on 2016/10/10.
 */
public class QueryGeneratorPlugin extends BaseGeneratorPlugin {

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

        String tableName = introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime();//数据库表名

        XmlElement parentElement = document.getRootElement();

        // 产生分页语句前半部分
        XmlElement answer = new XmlElement("select");
        answer.addAttribute(new Attribute("id", "query"));
        answer.addAttribute(new Attribute("resultMap", introspectedTable.getBaseResultMapId()));

        context.getCommentGenerator().addComment(answer);

        answer.addElement(new TextElement("select"));
        StringBuilder sb = new StringBuilder();
        answer.addElement(getBaseColumnListElement(introspectedTable));
        sb.setLength(0);
        sb.append("from "); //$NON-NLS-1$
        sb.append(introspectedTable
                .getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));
        //answer.addElement(getExampleIncludeElement(introspectedTable));

        parentElement.addElement(answer);

        return super.sqlMapDocumentGenerated(document, introspectedTable);
    }

    protected XmlElement getExampleIncludeElement(IntrospectedTable introspectedTable) {
        XmlElement ifElement = new XmlElement("if"); //$NON-NLS-1$
        ifElement.addAttribute(new Attribute("test", "_parameter != null")); //$NON-NLS-1$ //$NON-NLS-2$

        XmlElement includeElement = new XmlElement("include"); //$NON-NLS-1$
        includeElement.addAttribute(new Attribute("refid", //$NON-NLS-1$
                introspectedTable.getExampleWhereClauseId()));
        ifElement.addElement(includeElement);

        return ifElement;
    }


    private Method generateQueryAllMethod(IntrospectedTable introspectedTable) {

        Method method = new Method("query");
        method.setVisibility(JavaVisibility.PUBLIC);

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        returnType.addTypeArgument(listType);
        method.setReturnType(returnType);

        method.addParameter(new Parameter(new FullyQualifiedJavaType("PageBounds"), "pageBounds", "@Param(\"pageBounds\")"));

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);
        return method;
    }




}
