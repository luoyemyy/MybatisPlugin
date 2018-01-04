package com.github.luoyemyy.mybatis.plugin

import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.java.*
import org.mybatis.generator.internal.util.JavaBeansUtil.getGetterMethodName
import java.util.TreeSet

class MybatisInsertListPlugin : PluginAdapter() {
    override fun validate(warnings: MutableList<String>?): Boolean {
        return true
    }

    override fun clientGenerated(interfaze: Interface?, topLevelClass: TopLevelClass?, introspectedTable: IntrospectedTable?): Boolean {
        if (interfaze == null || introspectedTable == null) return true
        interfaze.addImportedType(FullyQualifiedJavaType("org.apache.ibatis.annotations.Param")) //$NON-NLS-1$
        interfaze.addImportedType(FullyQualifiedJavaType("java.util.List")) //$NON-NLS-1$


        val record = introspectedTable.rules.calculateAllFieldsClass()
        interfaze.addImportedType(record)

        val method = Method("insertList")
        method.visibility = JavaVisibility.PUBLIC
        method.returnType = FullyQualifiedJavaType.getIntInstance()
        method.addParameter(Parameter(FullyQualifiedJavaType("List<${record.shortName}>"), "list", "@Param(\"list\")"))
        method.addAnnotation("")

        val fqjt = FullyQualifiedJavaType(introspectedTable.myBatis3SqlProviderType)
        interfaze.addImportedType(FullyQualifiedJavaType("org.apache.ibatis.annotations.InsertProvider")) //$NON-NLS-1$
        val sb = StringBuilder()
        sb.append("@InsertProvider(type=") //$NON-NLS-1$
        sb.append(fqjt.shortName)
        sb.append(".class, method=\"") //$NON-NLS-1$
        sb.append("insertList")
        sb.append("\")") //$NON-NLS-1$
        method.addAnnotation(sb.toString())

        interfaze.addMethod(method)

        return true
    }

    override fun providerGenerated(topLevelClass: TopLevelClass?, introspectedTable: IntrospectedTable?): Boolean {
        if (topLevelClass == null || introspectedTable == null) return true
        val method = Method("insertList")
        method.returnType = FullyQualifiedJavaType.getStringInstance()
        method.visibility = JavaVisibility.PUBLIC
        method.addParameter(Parameter(FullyQualifiedJavaType("java.util.Map<java.lang.String, java.lang.Object>"), "parameter"))

        val record = introspectedTable.rules.calculateAllFieldsClass()
        method.addBodyLine(String.format("%s list = (%s) parameter.get(\"list\");", "List<${record.shortName}>", "List<${record.shortName}>"))

        method.addBodyLine("List<String> values = new ArrayList<>();")
        method.addBodyLine("for(${record.shortName} record : list) {")
        val properties = introspectedTable.allColumns.filterNot { it.isIdentity }.joinToString("+\",\"+", "\"(\"+", "+\")\"") { "record.${getGetterMethodName(it.javaProperty, it.fullyQualifiedJavaType)}()" }
        method.addBodyLine("values.add($properties);")
        method.addBodyLine("}")

        method.addBodyLine("StringBuilder sqlValues = new StringBuilder();")
        method.addBodyLine("for (int i = 0; i < values.size(); i++) {")
        method.addBodyLine("if (i > 0) {")
        method.addBodyLine("sqlValues.append(\",\");")
        method.addBodyLine("}")
        method.addBodyLine("sqlValues.append(values.get(i));")
        method.addBodyLine("}")


        val sql = "insert into ${introspectedTable.fullyQualifiedTable.introspectedTableName}(${introspectedTable.allColumns.filterNot { it.isIdentity }.joinToString(",") { it.actualColumnName }}) values"
        method.addBodyLine("return \"$sql\" + sqlValues.toString();")

        topLevelClass.addMethod(method)
        topLevelClass.addImportedType(FullyQualifiedJavaType("java.util.ArrayList"))
        return true
    }

}