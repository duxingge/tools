package com.ut.test.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author wangjiaxing
 * @Date 2021/12/2
 */
public class SqlSessionFactoryPostProcess extends InstantiationAwareBeanPostProcessorAdapter {


    private String dbBaseDir;

    public SqlSessionFactoryPostProcess(String dbBaseDir) {
        this.dbBaseDir = dbBaseDir;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        if (bean instanceof SqlSessionFactory) {
            SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) bean;
            try {
                initDb(sqlSessionFactory);
            } catch (Exception ignore) {
            }

        }

        return true;
    }

    public boolean initDb(SqlSessionFactory sqlSessionFactory) throws SQLException, IOException {
        Connection connection = sqlSessionFactory.openSession().getConnection();
        PreparedStatement ps = connection.prepareStatement("CREATE ALIAS TIMESTAMP_MILLSECONDS FOR \"java.lang.System.currentTimeMillis\"");
        ps.execute();
        ps.close();
        executeSql(dbBaseDir, sqlSessionFactory);
        return true;
    }

    public void executeSql(String baseDir, SqlSessionFactory sqlSessionFactory) throws SQLException, IOException {
        File file = null;
        try {
            file = Resources.getResourceAsFile(baseDir);
        } catch (IOException e) {
            return;
        }

        if (file.isDirectory()) {
            String[] list = file.list();
            for (String subFileName : list) {
                executeSql(baseDir + "/" + subFileName, sqlSessionFactory);
            }
        } else if (file.isFile() && file.toString().endsWith(".sql")) {
            executeSqlScript(sqlSessionFactory, baseDir);
        }

    }

    public void executeSqlScript(SqlSessionFactory sqlSessionFactory, final String... filePaths) throws SQLException, IOException {
        try (Connection connection = sqlSessionFactory.openSession().getConnection()) {
            for (String fileName : filePaths) {
                try (Reader reader = Resources.getResourceAsReader(fileName)) {
                    ScriptRunner runner = new ScriptRunner(connection);
                    runner.runScript(reader);
                }
            }
        }
    }
}
