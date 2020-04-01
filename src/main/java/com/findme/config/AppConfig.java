package com.findme.config;

import com.findme.controller.PostController;
import com.findme.dao.PostDAO;
import com.findme.dao.RelationshipDAO;
import com.findme.dao.UserDAO;
import com.findme.handler.*;
import com.findme.service.PostService;
import com.findme.service.RelationshipService;
import com.findme.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.persistence.EntityManagerFactory;
import java.util.Properties;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableWebMvc
@ComponentScan(basePackages = {"com.findme"})
public class AppConfig implements WebMvcConfigurer {

    private ApplicationContext applicationContext;

    @Autowired
    public AppConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public UserDAO userDAO(){
        return new UserDAO();
    }

    @Bean
    public UserService userService(){
        UserService userService;
        userService = new UserService(userDAO());
        return userService;
    }

    @Bean
    public PostDAO postDAO(){
        return new PostDAO();
    }

    @Bean
    public PostController postController(){
        PostController postController;
        postController = new PostController(postService(), userDAO());
        return postController;
    }

    @Bean
    public PostService postService(){
        PostService postService;
        postService = new PostService(postDAO(), relationshipDAO());
        return postService;
    }

    @Bean
    public RelationshipDAO relationshipDAO(){
        return new RelationshipDAO();
    }

    @Bean
    public HandlerChain handlerForUser(){
        return new HandlerChain(relationshipDAO());
    }

    @Bean
    public DeletedHandler deletedHandler(){
        return new DeletedHandler(relationshipDAO());
    }

    @Bean
    public CanceledHandler canceledHandler(){
        return new CanceledHandler(relationshipDAO());
    }

    @Bean
    public DeclinedHandler declinedHandler(){
        return new DeclinedHandler(relationshipDAO());
    }

    @Bean
    public AcceptedHandler acceptedHandler(){
        return new AcceptedHandler(relationshipDAO());
    }

    @Bean
    public RelationshipService relationshipService(){
        RelationshipService relationshipService;
        relationshipService = new RelationshipService(relationshipDAO(), userDAO(), handlerForUser());
        return relationshipService;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver(){
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setApplicationContext(applicationContext);
        templateResolver.setPrefix("classpath:/views/");
        templateResolver.setSuffix(".html");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(){
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setEnableSpringELCompiler(true);
        return templateEngine;
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry){
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        registry.viewResolver(resolver);
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.findme/models");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public DriverManagerDataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:@gromcode.cs1v5pkvp5p9.eu-central-1.rds.amazonaws.com:1521:ORCL");
        dataSource.setUsername("main");
        dataSource.setPassword("ifgjrkzr");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    private Properties additionalProperties(){
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.Oracle10gDialect");
        properties.setProperty("sessionFactory", "sessionFactory");
        properties.setProperty("hibernate.show_sql", "true");

        return properties;
    }
}