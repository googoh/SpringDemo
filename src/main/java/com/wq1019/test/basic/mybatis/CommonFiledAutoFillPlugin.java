package com.wq1019.test.basic.mybatis;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.wq1019.test.models.User;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class,Integer.class})})
@Intercepts({ @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }) })
public class CommonFiledAutoFillPlugin implements Interceptor {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private static int MAPPED_STATEMENT_INDEX = 0;
	private static int PARAMETER_INDEX = 1;

	public Object intercept(Invocation invocation) throws Throwable {

		MappedStatement ms = (MappedStatement) invocation.getArgs()[MAPPED_STATEMENT_INDEX];
		Object entity = invocation.getArgs()[PARAMETER_INDEX];

		if (entity != null && entity instanceof User) {
			BoundSql bSql = ms.getBoundSql(entity);
			String methodName = invocation.getMethod().getName();
			SqlCommandType sqlCommandType = ms.getSqlCommandType();
			if ("update".equals(methodName)) {
				Date currentDate = new Date(System.currentTimeMillis());
				if (SqlCommandType.INSERT.equals(sqlCommandType)) {
					Field f1 = entity.getClass().getSuperclass().getDeclaredField("createTime");
					f1.setAccessible(true);
					f1.set(entity, currentDate);
					Field f2 = entity.getClass().getSuperclass().getDeclaredField("modifyTime");
					f2.setAccessible(true);
					f2.set(entity, currentDate);
				} else if (SqlCommandType.UPDATE.equals(sqlCommandType)) {
					Field f2 = entity.getClass().getSuperclass().getDeclaredField("modifyTime");
					f2.setAccessible(true);
					f2.set(entity, currentDate);

					List<String> ignoreFields = Arrays.asList(new String[] { "CREATETIME" });
					UpdateSqlWrapper updater = UpdateSqlWrapper.parse(bSql.getSql());
					StringBuffer sqlBuff = new StringBuffer();
					sqlBuff.append("UPDATE ").append(updater.tableName).append(" SET ");
					for (String f : updater.fields) {
						if (ignoreFields.contains(f)) {
							continue;
						}
						sqlBuff.append(f).append("=?,");
					}
					sqlBuff.deleteCharAt(sqlBuff.length() - 1);
					sqlBuff.append(" WHERE ").append(updater.where);
					List<ParameterMapping> pmList = new ArrayList<>();
					for (ParameterMapping pm : bSql.getParameterMappings()) {
						if (ignoreFields.contains(pm.getProperty().toUpperCase())) {
							continue;
						}
						pmList.add(pm);
					}
					SqlSource sqlSource = new StaticSqlSource(ms.getConfiguration(), sqlBuff.toString(), pmList);
					MappedStatement newMs = buildMappedStatement(ms, sqlSource);

					invocation.getArgs()[MAPPED_STATEMENT_INDEX] = newMs;
				}
			}
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}

	private MappedStatement buildMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
		MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
			StringBuffer keyProperties = new StringBuffer();
			for (String keyProperty : ms.getKeyProperties()) {
				keyProperties.append(keyProperty).append(",");
			}
			keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());
		return builder.build();
	}

	private static class UpdateSqlWrapper {
		private String tableName = null;
		private String where = null;
		private List<String> fields = null;

		private static UpdateSqlWrapper parse(String sql) {

			// UPDATE t_user SET modifyTime = ?,name = ?,sex = ?,address = ? WHERE id = ?
			UpdateSqlWrapper wrapper = new UpdateSqlWrapper();
			sql = sql.toUpperCase();
			wrapper.tableName = sql.substring(0, sql.indexOf("SET")).replace("UPDATE", "").trim();
			wrapper.where = sql.substring(sql.indexOf("WHERE") + 5);
			wrapper.fields = new ArrayList<>();
			String temp = sql.substring(sql.indexOf("SET") + 3, sql.indexOf("WHERE")).trim();
			String[] tempArray = temp.split(",");
			for (String term : tempArray) {
				wrapper.fields.add(term.substring(0, term.indexOf("=")).trim());
			}
			return wrapper;
		}

	}
}
