package com.lin.common.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

//@Configuration
//扫描 Mapper 接口并容器管理
@EnableTransactionManagement
@MapperScan(basePackages = StgDatasourceConfig.PACKAGE, sqlSessionFactoryRef = "stgSqlSessionFactory")
public class StgDatasourceConfig {
// implements TransactionManagementConfigurer

    // 精确到 master 目录，以便跟其他数据源隔离
    static final String PACKAGE = "com.lin.common.dao.stgDao";
    static final String MAPPER_LOCATION = "classpath:mapper/stg/*.xml";


    @Bean
    // 读配置文件
    @ConfigurationProperties(prefix = "spring.datasource.mysql")
    public DataSource stgDataSource() {
        //jdbc配置
        DruidDataSource dataSource = new DruidDataSource();
        return dataSource;
    }


    //    @Bean
//    public DataSourceTransactionManager transactionManager() {
//        return new DataSourceTransactionManager(stgDataSource());
//    }
    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("stgDataSource") DataSource stgDataSource) {
        return new DataSourceTransactionManager(stgDataSource);
    }

    @Bean
    public SqlSessionFactory stgSqlSessionFactory() {
        try {
            final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(stgDataSource());
//            sessionFactory.setTypeAliasesPackage("com.lin.common.dto");
//            sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(DatasourceConfig.MAPPER_LOCATION));
            //添加PageHelper插件
            Interceptor interceptor = new PageInterceptor();
            Properties properties = new Properties();
            //数据库
            properties.setProperty("helperDialect", "mysql");
            //是否将参数offset作为PageNum使用
            properties.setProperty("offsetAsPageNum", "true");
            //是否进行count查询
            properties.setProperty("rowBoundsWithCount", "true");
            //是否分页合理化
            properties.setProperty("reasonable", "false");
            //
            interceptor.setProperties(properties);
            //添加插件
            sessionFactory.setPlugins(new Interceptor[]{interceptor});

            //添加mapper操作数据库XML目录
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            sessionFactory.setMapperLocations(resolver.getResources(MAPPER_LOCATION));
            return sessionFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }


    /*spring通过SqlSessionTemplate对象去操作sqlsession语句
    @Bean
    public SqlSessionTemplate sqlSessionTemplate() {
        SqlSessionTemplate sqlSessionTemplate = new SqlSessionTemplate(sqlSessionFactory1());
        return sqlSessionTemplate;
    }
       */
    //配置事务管理器
    /**
     * Transaction 相关配置
     * 因为有两个数据源，所有使用ChainedTransactionManager把两个DataSourceTransactionManager包括在一起。

     @Override public PlatformTransactionManager annotationDrivenTransactionManager() {
     DataSourceTransactionManager dtm1 = new DataSourceTransactionManager(dataSource1());
     DataSourceTransactionManager dtm2 = new DataSourceTransactionManager(dataSource2());

     ChainedTransactionManager ctm = new ChainedTransactionManager(dtm1, dtm2);
     return ctm;
     }
     */


    /**
     * 上面的配置我们主要通过mybatis的SqlSessionFactoryBean来获取SqlsessionFactory工厂类，
     * 通过工厂类获取SqlSessionTemplate对象操作sqlsession语句，
     * 值得注意的是SqlSessionTemplate是线程安全的。
     * <p>
     * 在 MyBatis 中,你可以使用 SqlSessionFactory 来创建 SqlSession。一旦你获得一个 session 之后,
     * 你可以使用它来执行映射语句,提交或回滚连接,最后,当不再需要它的时 候, 你可以关闭 session。
     * <p>
     * SqlSessionTemplate 是 MyBatis-Spring 的核心。 这个类负责管理 MyBatis 的 SqlSession,
     * 调用 MyBatis 的 SQL 方法, 翻译异常。 SqlSessionTemplate 是线程安全的, 可以被多个 DAO 所共享使用。
     */

    //配置Druid的监控
    //1、配置一个管理后台的Servlet
    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String, String> initParams = new HashMap<>();

        initParams.put("loginUsername", "admin");
        initParams.put("loginPassword", "123456");
        initParams.put("allow", "");//默认就是允许所有访问
//        initParams.put("deny","192.168.15.21");

        bean.setInitParameters(initParams);
        return bean;
    }


    //2、配置一个web监控的filter
    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());

        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.css,/druid/*");

        bean.setInitParameters(initParams);

        bean.setUrlPatterns(Arrays.asList("/*"));

        return bean;
    }
}