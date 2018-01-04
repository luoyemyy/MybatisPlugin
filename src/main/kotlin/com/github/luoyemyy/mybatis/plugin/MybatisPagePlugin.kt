package com.github.luoyemyy.mybatis.plugin

import org.mybatis.generator.api.IntrospectedTable
import org.mybatis.generator.api.PluginAdapter
import org.mybatis.generator.api.dom.java.*

class MybatisPagePlugin : PluginAdapter() {
    override fun validate(warnings: MutableList<String>?): Boolean {
        return true
    }

    override fun modelExampleClassGenerated(topLevelClass: TopLevelClass?, introspectedTable: IntrospectedTable?): Boolean {

        val field = Field()
        field.visibility = JavaVisibility.PROTECTED
        field.type = FullyQualifiedJavaType.getStringInstance()
        field.name = "limit"
        topLevelClass?.addField(field)

        var method = Method()
        method.visibility = JavaVisibility.PUBLIC
        method.name = "setLimit" //$NON-NLS-1$
        method.addParameter(Parameter(FullyQualifiedJavaType.getStringInstance(), "limit"))
        method.addBodyLine("this.limit = limit;")
        topLevelClass?.addMethod(method)

        method = Method()
        method.visibility = JavaVisibility.PUBLIC
        method.returnType = FullyQualifiedJavaType.getStringInstance()
        method.name = "getLimit" //$NON-NLS-1$
        method.addBodyLine("return limit;") //$NON-NLS-1$
        topLevelClass?.addMethod(method)

        return true
    }


    override fun providerSelectByExampleWithBLOBsMethodGenerated(method: Method?, topLevelClass: TopLevelClass?, introspectedTable: IntrospectedTable?): Boolean {
        if (method == null) {
            return true
        }
        method.bodyLines.removeAt(method.bodyLines.size - 1)
        method.addBodyLine("String sql = SQL();")
        method.addBodyLine("if (example != null && example.getLimit() != null) {")
        method.addBodyLine("sql += example.getLimit();") //$NON-NLS-1$
        method.addBodyLine("}") //$NON-NLS-1$

        method.addBodyLine("") //$NON-NLS-1$
        method.addBodyLine("return sql;") //$NON-NLS-1$
        return true
    }

    override fun providerSelectByExampleWithoutBLOBsMethodGenerated(method: Method?, topLevelClass: TopLevelClass?, introspectedTable: IntrospectedTable?): Boolean {
        if (method == null) {
            return true
        }
        method.bodyLines.removeAt(method.bodyLines.size - 1)
        method.addBodyLine("String sql = SQL();")
        method.addBodyLine("if (example != null && example.getLimit() != null) {")
        method.addBodyLine("sql += example.getLimit();") //$NON-NLS-1$
        method.addBodyLine("}") //$NON-NLS-1$

        method.addBodyLine("") //$NON-NLS-1$
        method.addBodyLine("return sql;") //$NON-NLS-1$
        return true
    }
}