Mybatis运行原理解析：

一、通过SqlSessionFactoryBuilder的build方法构建SqlSessionFactory
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        SqlSessionFactory var5;
        try {
            // 读取配置文件，并生成Configuration对象，设置Configuration对象的默认值
            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
            // parser.parse()，解析Mybatis主配置文件，将所有解析出的信息保存到Configuration中，并返回Configuration
            // 调用build方法，返回DefaultSqlSessionFactory，DefaultSqlSessionFactory中保存了final修饰的Configuration
            var5 = this.build(parser.parse());
        } catch (Exception var14) {
            throw ExceptionFactory.wrapException("Error building SqlSession.", var14);
        } finally {
            ErrorContext.instance().reset();

            try {
                inputStream.close();
            } catch (IOException var13) {
            }
        }
        return var5;
    }

    private void parseConfiguration(XNode root) {
            try {
                this.propertiesElement(root.evalNode("properties"));
                Properties settings = this.settingsAsProperties(root.evalNode("settings"));
                this.loadCustomVfs(settings);
                this.loadCustomLogImpl(settings);
                this.typeAliasesElement(root.evalNode("typeAliases"));
                this.pluginElement(root.evalNode("plugins"));
                this.objectFactoryElement(root.evalNode("objectFactory"));
                this.objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
                this.reflectorFactoryElement(root.evalNode("reflectorFactory"));
                this.settingsElement(settings);
                this.environmentsElement(root.evalNode("environments"));
                this.databaseIdProviderElement(root.evalNode("databaseIdProvider"));
                this.typeHandlerElement(root.evalNode("typeHandlers"));
                this.mapperElement(root.evalNode("mappers"));
            } catch (Exception var3) {
                throw new BuilderException("Error parsing SQL Mapper Configuration. Cause: " + var3, var3);
            }
        }

    public Configuration() {
            this.safeResultHandlerEnabled = true;
            this.multipleResultSetsEnabled = true;
            this.useColumnLabel = true;
            this.cacheEnabled = true;
            this.useActualParamName = true;
            this.localCacheScope = LocalCacheScope.SESSION;
            this.jdbcTypeForNull = JdbcType.OTHER;
            this.lazyLoadTriggerMethods = new HashSet(Arrays.asList("equals", "clone", "hashCode", "toString"));
            this.defaultExecutorType = ExecutorType.SIMPLE;
            this.autoMappingBehavior = AutoMappingBehavior.PARTIAL;
            this.autoMappingUnknownColumnBehavior = AutoMappingUnknownColumnBehavior.NONE;
            this.variables = new Properties();
            this.reflectorFactory = new DefaultReflectorFactory();
            this.objectFactory = new DefaultObjectFactory();
            this.objectWrapperFactory = new DefaultObjectWrapperFactory();
            this.lazyLoadingEnabled = false;
            this.proxyFactory = new JavassistProxyFactory();
            this.mapperRegistry = new MapperRegistry(this);
            this.interceptorChain = new InterceptorChain();
            this.typeHandlerRegistry = new TypeHandlerRegistry();
            this.typeAliasRegistry = new TypeAliasRegistry();
            this.languageRegistry = new LanguageDriverRegistry();
            this.mappedStatements = (new Configuration.StrictMap("Mapped Statements collection")).conflictMessageProducer((savedValue, targetValue) -> {
                return ". please check " + savedValue.getResource() + " and " + targetValue.getResource();
            });
            this.caches = new Configuration.StrictMap("Caches collection");
            this.resultMaps = new Configuration.StrictMap("Result Maps collection");
            this.parameterMaps = new Configuration.StrictMap("Parameter Maps collection");
            this.keyGenerators = new Configuration.StrictMap("Key Generators collection");
            this.loadedResources = new HashSet();
            this.sqlFragments = new Configuration.StrictMap("XML fragments parsed from previous mappers");
            this.incompleteStatements = new LinkedList();
            this.incompleteCacheRefs = new LinkedList();
            this.incompleteResultMaps = new LinkedList();
            this.incompleteMethods = new LinkedList();
            this.cacheRefMap = new HashMap();
            this.typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
            this.typeAliasRegistry.registerAlias("MANAGED", ManagedTransactionFactory.class);
            this.typeAliasRegistry.registerAlias("JNDI", JndiDataSourceFactory.class);
            this.typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
            this.typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);
            this.typeAliasRegistry.registerAlias("PERPETUAL", PerpetualCache.class);
            this.typeAliasRegistry.registerAlias("FIFO", FifoCache.class);
            this.typeAliasRegistry.registerAlias("LRU", LruCache.class);
            this.typeAliasRegistry.registerAlias("SOFT", SoftCache.class);
            this.typeAliasRegistry.registerAlias("WEAK", WeakCache.class);
            this.typeAliasRegistry.registerAlias("DB_VENDOR", VendorDatabaseIdProvider.class);
            this.typeAliasRegistry.registerAlias("XML", XMLLanguageDriver.class);
            this.typeAliasRegistry.registerAlias("RAW", RawLanguageDriver.class);
            this.typeAliasRegistry.registerAlias("SLF4J", Slf4jImpl.class);
            this.typeAliasRegistry.registerAlias("COMMONS_LOGGING", JakartaCommonsLoggingImpl.class);
            this.typeAliasRegistry.registerAlias("LOG4J", Log4jImpl.class);
            this.typeAliasRegistry.registerAlias("LOG4J2", Log4j2Impl.class);
            this.typeAliasRegistry.registerAlias("JDK_LOGGING", Jdk14LoggingImpl.class);
            this.typeAliasRegistry.registerAlias("STDOUT_LOGGING", StdOutImpl.class);
            this.typeAliasRegistry.registerAlias("NO_LOGGING", NoLoggingImpl.class);
            this.typeAliasRegistry.registerAlias("CGLIB", CglibProxyFactory.class);
            this.typeAliasRegistry.registerAlias("JAVASSIST", JavassistProxyFactory.class);
            this.languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
            this.languageRegistry.register(RawLanguageDriver.class);
        }

        public SqlSessionFactory build(Configuration config) {
           return new DefaultSqlSessionFactory(config);
        }

二、获取SqlSession
    sqlSessionFactory.openSession();
    public SqlSession openSession() {
        return this.openSessionFromDataSource(this.configuration.getDefaultExecutorType(), (TransactionIsolationLevel)null, false);
    }

    private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
        Transaction tx = null;
        // 定义DefaultSqlSession
        DefaultSqlSession var8;
        try {
            Environment environment = this.configuration.getEnvironment();
            // 获取事务工场
            TransactionFactory transactionFactory = this.getTransactionFactoryFromEnvironment(environment);
            // 新建事务
            tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
            // 根据配置获取Executor执行器，Executo实现了与数据库的交互
            Executor executor = this.configuration.newExecutor(tx, execType);
            // 返回DefaultSqlSession
            var8 = new DefaultSqlSession(this.configuration, executor, autoCommit);
        } catch (Exception var12) {
            this.closeTransaction(tx);
            throw ExceptionFactory.wrapException("Error opening session.  Cause: " + var12, var12);
        } finally {
            ErrorContext.instance().reset();
        }
        return var8;
    }

    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
            executorType = executorType == null ? this.defaultExecutorType : executorType;
            executorType = executorType == null ? ExecutorType.SIMPLE : executorType;
            Object executor;
            if (ExecutorType.BATCH == executorType) {
                executor = new BatchExecutor(this, transaction);
            } else if (ExecutorType.REUSE == executorType) {
                executor = new ReuseExecutor(this, transaction);
            } else {
                executor = new SimpleExecutor(this, transaction);
            }

            if (this.cacheEnabled) {
                executor = new CachingExecutor((Executor)executor);
            }
            // 拦截器
            Executor executor = (Executor)this.interceptorChain.pluginAll(executor);
            return executor;
        }

        public Object pluginAll(Object target) {
            Interceptor interceptor;
            // 执行拦截器的plugin方法
            for(Iterator var2 = this.interceptors.iterator(); var2.hasNext(); target = interceptor.plugin(target)) {
                interceptor = (Interceptor)var2.next();
            }
            return target;
        }

三、通过SqlSession对象获取mapper,mapper是一个代理对象
    RoleMapper mapper = sqlSession.getMapper(RoleMapper.class);

    代理对象，实现了InvocationHandler接口
    public class MapperProxy<T> implements InvocationHandler, Serializable {
        private static final long serialVersionUID = -6424540398559729838L;
        private final SqlSession sqlSession;
        private final Class<T> mapperInterface;
        private final Map<Method, MapperMethod> methodCache;

        public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
            this.sqlSession = sqlSession;
            this.mapperInterface = mapperInterface;
            this.methodCache = methodCache;
        }
        // 代理对象实际执行的方法
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                // Object.class全类名是否等于Mapper的全类名
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }
                // 是否默认方法
                if (method.isDefault()) {
                    return this.invokeDefaultMethod(proxy, method, args);
                }
            } catch (Throwable var5) {
                throw ExceptionUtil.unwrapThrowable(var5);
            }
            // 创建MapperMethod
            MapperMethod mapperMethod = this.cachedMapperMethod(method);
            // 调用mapperMethod.execute
            return mapperMethod.execute(this.sqlSession, args);
        }

        private MapperMethod cachedMapperMethod(Method method) {
            return (MapperMethod)this.methodCache.computeIfAbsent(method, (k) -> {
                return new MapperMethod(this.mapperInterface, method, this.sqlSession.getConfiguration());
            });
        }

        private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
            Constructor<Lookup> constructor = Lookup.class.getDeclaredConstructor(Class.class, Integer.TYPE);
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }

            Class<?> declaringClass = method.getDeclaringClass();
            return ((Lookup)constructor.newInstance(declaringClass, 15)).unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
        }
    }
4.通过Mapper调用执行具体的方法
    Role role = mapper.getRole(4L);

    public class MapperMethod {
        private final MapperMethod.SqlCommand command;
        private final MapperMethod.MethodSignature method;

        public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
            this.command = new MapperMethod.SqlCommand(config, mapperInterface, method);
            this.method = new MapperMethod.MethodSignature(config, mapperInterface, method);
        }

        public Object execute(SqlSession sqlSession, Object[] args) {
            Object result;
            Object param;
            // 命令类型
            switch(this.command.getType()) {
            case INSERT:
                param = this.method.convertArgsToSqlCommandParam(args);
                result = this.rowCountResult(sqlSession.insert(this.command.getName(), param));
                break;
            case UPDATE:
                param = this.method.convertArgsToSqlCommandParam(args);
                result = this.rowCountResult(sqlSession.update(this.command.getName(), param));
                break;
            case DELETE:
                param = this.method.convertArgsToSqlCommandParam(args);
                result = this.rowCountResult(sqlSession.delete(this.command.getName(), param));
                break;
            case SELECT:
                if (this.method.returnsVoid() && this.method.hasResultHandler()) {
                    this.executeWithResultHandler(sqlSession, args);
                    result = null;
                } else if (this.method.returnsMany()) {
                    result = this.executeForMany(sqlSession, args);
                } else if (this.method.returnsMap()) {
                    result = this.executeForMap(sqlSession, args);
                } else if (this.method.returnsCursor()) {
                    result = this.executeForCursor(sqlSession, args);
                } else {
                    // 返回参数，如果是单个参数直接返回，如果是多个封装成Map返回
                    param = this.method.convertArgsToSqlCommandParam(args);
                    //
                    result = sqlSession.selectOne(this.command.getName(), param);
                    if (this.method.returnsOptional() && (result == null || !this.method.getReturnType().equals(result.getClass()))) {
                        result = Optional.ofNullable(result);
                    }
                }
                break;
            case FLUSH:
                result = sqlSession.flushStatements();
                break;
            default:
                throw new BindingException("Unknown execution method for: " + this.command.getName());
            }

            if (result == null && this.method.getReturnType().isPrimitive() && !this.method.returnsVoid()) {
                throw new BindingException("Mapper method '" + this.command.getName() + " attempted to return null from a method with a primitive return type (" + this.method.getReturnType() + ").");
            } else {
                return result;
            }
        }

        private Object rowCountResult(int rowCount) {
            Object result;
            if (this.method.returnsVoid()) {
                result = null;
            } else if (!Integer.class.equals(this.method.getReturnType()) && !Integer.TYPE.equals(this.method.getReturnType())) {
                if (!Long.class.equals(this.method.getReturnType()) && !Long.TYPE.equals(this.method.getReturnType())) {
                    if (!Boolean.class.equals(this.method.getReturnType()) && !Boolean.TYPE.equals(this.method.getReturnType())) {
                        throw new BindingException("Mapper method '" + this.command.getName() + "' has an unsupported return type: " + this.method.getReturnType());
                    }

                    result = rowCount > 0;
                } else {
                    result = (long)rowCount;
                }
            } else {
                result = rowCount;
            }

            return result;
        }

        private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {
            MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(this.command.getName());
            if (!StatementType.CALLABLE.equals(ms.getStatementType()) && Void.TYPE.equals(((ResultMap)ms.getResultMaps().get(0)).getType())) {
                throw new BindingException("method " + this.command.getName() + " needs either a @ResultMap annotation, a @ResultType annotation, or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
            } else {
                Object param = this.method.convertArgsToSqlCommandParam(args);
                if (this.method.hasRowBounds()) {
                    RowBounds rowBounds = this.method.extractRowBounds(args);
                    sqlSession.select(this.command.getName(), param, rowBounds, this.method.extractResultHandler(args));
                } else {
                    sqlSession.select(this.command.getName(), param, this.method.extractResultHandler(args));
                }

            }
        }

        private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
            Object param = this.method.convertArgsToSqlCommandParam(args);
            List result;
            if (this.method.hasRowBounds()) {
                RowBounds rowBounds = this.method.extractRowBounds(args);
                result = sqlSession.selectList(this.command.getName(), param, rowBounds);
            } else {
                result = sqlSession.selectList(this.command.getName(), param);
            }

            if (!this.method.getReturnType().isAssignableFrom(result.getClass())) {
                return this.method.getReturnType().isArray() ? this.convertToArray(result) : this.convertToDeclaredCollection(sqlSession.getConfiguration(), result);
            } else {
                return result;
            }
        }

        private <T> Cursor<T> executeForCursor(SqlSession sqlSession, Object[] args) {
            Object param = this.method.convertArgsToSqlCommandParam(args);
            Cursor result;
            if (this.method.hasRowBounds()) {
                RowBounds rowBounds = this.method.extractRowBounds(args);
                result = sqlSession.selectCursor(this.command.getName(), param, rowBounds);
            } else {
                result = sqlSession.selectCursor(this.command.getName(), param);
            }

            return result;
        }

        private <E> Object convertToDeclaredCollection(Configuration config, List<E> list) {
            Object collection = config.getObjectFactory().create(this.method.getReturnType());
            MetaObject metaObject = config.newMetaObject(collection);
            metaObject.addAll(list);
            return collection;
        }

        private <E> Object convertToArray(List<E> list) {
            Class<?> arrayComponentType = this.method.getReturnType().getComponentType();
            Object array = Array.newInstance(arrayComponentType, list.size());
            if (!arrayComponentType.isPrimitive()) {
                return list.toArray((Object[])array);
            } else {
                for(int i = 0; i < list.size(); ++i) {
                    Array.set(array, i, list.get(i));
                }

                return array;
            }
        }

        private <K, V> Map<K, V> executeForMap(SqlSession sqlSession, Object[] args) {
            Object param = this.method.convertArgsToSqlCommandParam(args);
            Map result;
            if (this.method.hasRowBounds()) {
                RowBounds rowBounds = this.method.extractRowBounds(args);
                result = sqlSession.selectMap(this.command.getName(), param, this.method.getMapKey(), rowBounds);
            } else {
                result = sqlSession.selectMap(this.command.getName(), param, this.method.getMapKey());
            }

            return result;
        }

        public static class MethodSignature {
            private final boolean returnsMany;
            private final boolean returnsMap;
            private final boolean returnsVoid;
            private final boolean returnsCursor;
            private final boolean returnsOptional;
            private final Class<?> returnType;
            private final String mapKey;
            private final Integer resultHandlerIndex;
            private final Integer rowBoundsIndex;
            private final ParamNameResolver paramNameResolver;

            public MethodSignature(Configuration configuration, Class<?> mapperInterface, Method method) {
                Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
                if (resolvedReturnType instanceof Class) {
                    this.returnType = (Class)resolvedReturnType;
                } else if (resolvedReturnType instanceof ParameterizedType) {
                    this.returnType = (Class)((ParameterizedType)resolvedReturnType).getRawType();
                } else {
                    this.returnType = method.getReturnType();
                }

                this.returnsVoid = Void.TYPE.equals(this.returnType);
                this.returnsMany = configuration.getObjectFactory().isCollection(this.returnType) || this.returnType.isArray();
                this.returnsCursor = Cursor.class.equals(this.returnType);
                this.returnsOptional = Optional.class.equals(this.returnType);
                this.mapKey = this.getMapKey(method);
                this.returnsMap = this.mapKey != null;
                this.rowBoundsIndex = this.getUniqueParamIndex(method, RowBounds.class);
                this.resultHandlerIndex = this.getUniqueParamIndex(method, ResultHandler.class);
                this.paramNameResolver = new ParamNameResolver(configuration, method);
            }

            public Object convertArgsToSqlCommandParam(Object[] args) {
                return this.paramNameResolver.getNamedParams(args);
            }

            public boolean hasRowBounds() {
                return this.rowBoundsIndex != null;
            }

            public RowBounds extractRowBounds(Object[] args) {
                return this.hasRowBounds() ? (RowBounds)args[this.rowBoundsIndex] : null;
            }

            public boolean hasResultHandler() {
                return this.resultHandlerIndex != null;
            }

            public ResultHandler extractResultHandler(Object[] args) {
                return this.hasResultHandler() ? (ResultHandler)args[this.resultHandlerIndex] : null;
            }

            public String getMapKey() {
                return this.mapKey;
            }

            public Class<?> getReturnType() {
                return this.returnType;
            }

            public boolean returnsMany() {
                return this.returnsMany;
            }

            public boolean returnsMap() {
                return this.returnsMap;
            }

            public boolean returnsVoid() {
                return this.returnsVoid;
            }

            public boolean returnsCursor() {
                return this.returnsCursor;
            }

            public boolean returnsOptional() {
                return this.returnsOptional;
            }

            private Integer getUniqueParamIndex(Method method, Class<?> paramType) {
                Integer index = null;
                Class<?>[] argTypes = method.getParameterTypes();

                for(int i = 0; i < argTypes.length; ++i) {
                    if (paramType.isAssignableFrom(argTypes[i])) {
                        if (index != null) {
                            throw new BindingException(method.getName() + " cannot have multiple " + paramType.getSimpleName() + " parameters");
                        }

                        index = i;
                    }
                }

                return index;
            }

            private String getMapKey(Method method) {
                String mapKey = null;
                if (Map.class.isAssignableFrom(method.getReturnType())) {
                    MapKey mapKeyAnnotation = (MapKey)method.getAnnotation(MapKey.class);
                    if (mapKeyAnnotation != null) {
                        mapKey = mapKeyAnnotation.value();
                    }
                }

                return mapKey;
            }
        }

        public static class SqlCommand {
            private final String name;
            private final SqlCommandType type;

            public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
                String methodName = method.getName();
                Class<?> declaringClass = method.getDeclaringClass();
                MappedStatement ms = this.resolveMappedStatement(mapperInterface, methodName, declaringClass, configuration);
                if (ms == null) {
                    if (method.getAnnotation(Flush.class) == null) {
                        throw new BindingException("Invalid bound statement (not found): " + mapperInterface.getName() + "." + methodName);
                    }

                    this.name = null;
                    this.type = SqlCommandType.FLUSH;
                } else {
                    this.name = ms.getId();
                    this.type = ms.getSqlCommandType();
                    if (this.type == SqlCommandType.UNKNOWN) {
                        throw new BindingException("Unknown execution method for: " + this.name);
                    }
                }

            }

            public String getName() {
                return this.name;
            }

            public SqlCommandType getType() {
                return this.type;
            }

            private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName, Class<?> declaringClass, Configuration configuration) {
                String statementId = mapperInterface.getName() + "." + methodName;
                if (configuration.hasStatement(statementId)) {
                    return configuration.getMappedStatement(statementId);
                } else if (mapperInterface.equals(declaringClass)) {
                    return null;
                } else {
                    Class[] var6 = mapperInterface.getInterfaces();
                    int var7 = var6.length;

                    for(int var8 = 0; var8 < var7; ++var8) {
                        Class<?> superInterface = var6[var8];
                        if (declaringClass.isAssignableFrom(superInterface)) {
                            MappedStatement ms = this.resolveMappedStatement(superInterface, methodName, declaringClass, configuration);
                            if (ms != null) {
                                return ms;
                            }
                        }
                    }

                    return null;
                }
            }
        }

        public static class ParamMap<V> extends HashMap<String, V> {
            private static final long serialVersionUID = -2212268410512043556L;

            public ParamMap() {
            }

            public V get(Object key) {
                if (!super.containsKey(key)) {
                    throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + this.keySet());
                } else {
                    return super.get(key);
                }
            }
        }
    }


    ParamNameResolver类的
    public Object getNamedParams(Object[] args) {
        int paramCount = this.names.size();
        if (args != null && paramCount != 0) {
            // 没有@Param注解，并且只有一个参数
            if (!this.hasParamAnnotation && paramCount == 1) {
                return args[(Integer)this.names.firstKey()];
            } else {
                // 将参数放到Map中
                Map<String, Object> param = new ParamMap();
                int i = 0;

                for(Iterator var5 = this.names.entrySet().iterator(); var5.hasNext(); ++i) {
                    Entry<Integer, String> entry = (Entry)var5.next();
                    param.put((String)entry.getValue(), args[(Integer)entry.getKey()]);
                    String genericParamName = "param" + String.valueOf(i + 1);
                    if (!this.names.containsValue(genericParamName)) {
                        param.put(genericParamName, args[(Integer)entry.getKey()]);
                    }
                }

                return param;
            }
        } else {
            return null;
        }
    }


    //
    // Source code recreated from a .class file by IntelliJ IDEA
    // (powered by Fernflower decompiler)
    //

    package org.apache.ibatis.session.defaults;

    import java.io.IOException;
    import java.sql.Connection;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.Collection;
    import java.util.HashMap;
    import java.util.Iterator;
    import java.util.List;
    import java.util.Map;
    import org.apache.ibatis.binding.BindingException;
    import org.apache.ibatis.cursor.Cursor;
    import org.apache.ibatis.exceptions.ExceptionFactory;
    import org.apache.ibatis.exceptions.TooManyResultsException;
    import org.apache.ibatis.executor.BatchResult;
    import org.apache.ibatis.executor.ErrorContext;
    import org.apache.ibatis.executor.Executor;
    import org.apache.ibatis.executor.result.DefaultMapResultHandler;
    import org.apache.ibatis.executor.result.DefaultResultContext;
    import org.apache.ibatis.mapping.MappedStatement;
    import org.apache.ibatis.session.Configuration;
    import org.apache.ibatis.session.ResultHandler;
    import org.apache.ibatis.session.RowBounds;
    import org.apache.ibatis.session.SqlSession;

    public class DefaultSqlSession implements SqlSession {
        private final Configuration configuration;
        private final Executor executor;
        private final boolean autoCommit;
        private boolean dirty;
        private List<Cursor<?>> cursorList;

        public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
            this.configuration = configuration;
            this.executor = executor;
            this.dirty = false;
            this.autoCommit = autoCommit;
        }

        public DefaultSqlSession(Configuration configuration, Executor executor) {
            this(configuration, executor, false);
        }

        public <T> T selectOne(String statement) {
            return this.selectOne(statement, (Object)null);
        }

        public <T> T selectOne(String statement, Object parameter) {
            List<T> list = this.selectList(statement, parameter);
            if (list.size() == 1) {
                return list.get(0);
            } else if (list.size() > 1) {
                throw new TooManyResultsException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
            } else {
                return null;
            }
        }

        public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
            return this.selectMap(statement, (Object)null, mapKey, RowBounds.DEFAULT);
        }

        public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
            return this.selectMap(statement, parameter, mapKey, RowBounds.DEFAULT);
        }

        public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
            List<? extends V> list = this.selectList(statement, parameter, rowBounds);
            DefaultMapResultHandler<K, V> mapResultHandler = new DefaultMapResultHandler(mapKey, this.configuration.getObjectFactory(), this.configuration.getObjectWrapperFactory(), this.configuration.getReflectorFactory());
            DefaultResultContext<V> context = new DefaultResultContext();
            Iterator var8 = list.iterator();

            while(var8.hasNext()) {
                V o = var8.next();
                context.nextResultObject(o);
                mapResultHandler.handleResult(context);
            }

            return mapResultHandler.getMappedResults();
        }

        public <T> Cursor<T> selectCursor(String statement) {
            return this.selectCursor(statement, (Object)null);
        }

        public <T> Cursor<T> selectCursor(String statement, Object parameter) {
            return this.selectCursor(statement, parameter, RowBounds.DEFAULT);
        }

        public <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds) {
            Cursor var6;
            try {
                MappedStatement ms = this.configuration.getMappedStatement(statement);
                Cursor<T> cursor = this.executor.queryCursor(ms, this.wrapCollection(parameter), rowBounds);
                this.registerCursor(cursor);
                var6 = cursor;
            } catch (Exception var10) {
                throw ExceptionFactory.wrapException("Error querying database.  Cause: " + var10, var10);
            } finally {
                ErrorContext.instance().reset();
            }

            return var6;
        }

        public <E> List<E> selectList(String statement) {
            return this.selectList(statement, (Object)null);
        }

        public <E> List<E> selectList(String statement, Object parameter) {
            return this.selectList(statement, parameter, RowBounds.DEFAULT);
        }

        public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
            List var5;
            try {
                MappedStatement ms = this.configuration.getMappedStatement(statement);
                var5 = this.executor.query(ms, this.wrapCollection(parameter), rowBounds, Executor.NO_RESULT_HANDLER);
            } catch (Exception var9) {
                throw ExceptionFactory.wrapException("Error querying database.  Cause: " + var9, var9);
            } finally {
                ErrorContext.instance().reset();
            }

            return var5;
        }

        public void select(String statement, Object parameter, ResultHandler handler) {
            this.select(statement, parameter, RowBounds.DEFAULT, handler);
        }

        public void select(String statement, ResultHandler handler) {
            this.select(statement, (Object)null, RowBounds.DEFAULT, handler);
        }

        public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
            try {
                MappedStatement ms = this.configuration.getMappedStatement(statement);
                this.executor.query(ms, this.wrapCollection(parameter), rowBounds, handler);
            } catch (Exception var9) {
                throw ExceptionFactory.wrapException("Error querying database.  Cause: " + var9, var9);
            } finally {
                ErrorContext.instance().reset();
            }

        }

        public int insert(String statement) {
            return this.insert(statement, (Object)null);
        }

        public int insert(String statement, Object parameter) {
            return this.update(statement, parameter);
        }

        public int update(String statement) {
            return this.update(statement, (Object)null);
        }

        public int update(String statement, Object parameter) {
            int var4;
            try {
                this.dirty = true;
                MappedStatement ms = this.configuration.getMappedStatement(statement);
                var4 = this.executor.update(ms, this.wrapCollection(parameter));
            } catch (Exception var8) {
                throw ExceptionFactory.wrapException("Error updating database.  Cause: " + var8, var8);
            } finally {
                ErrorContext.instance().reset();
            }

            return var4;
        }

        public int delete(String statement) {
            return this.update(statement, (Object)null);
        }

        public int delete(String statement, Object parameter) {
            return this.update(statement, parameter);
        }

        public void commit() {
            this.commit(false);
        }

        public void commit(boolean force) {
            try {
                this.executor.commit(this.isCommitOrRollbackRequired(force));
                this.dirty = false;
            } catch (Exception var6) {
                throw ExceptionFactory.wrapException("Error committing transaction.  Cause: " + var6, var6);
            } finally {
                ErrorContext.instance().reset();
            }

        }

        public void rollback() {
            this.rollback(false);
        }

        public void rollback(boolean force) {
            try {
                this.executor.rollback(this.isCommitOrRollbackRequired(force));
                this.dirty = false;
            } catch (Exception var6) {
                throw ExceptionFactory.wrapException("Error rolling back transaction.  Cause: " + var6, var6);
            } finally {
                ErrorContext.instance().reset();
            }

        }

        public List<BatchResult> flushStatements() {
            List var1;
            try {
                var1 = this.executor.flushStatements();
            } catch (Exception var5) {
                throw ExceptionFactory.wrapException("Error flushing statements.  Cause: " + var5, var5);
            } finally {
                ErrorContext.instance().reset();
            }

            return var1;
        }

        public void close() {
            try {
                this.executor.close(this.isCommitOrRollbackRequired(false));
                this.closeCursors();
                this.dirty = false;
            } finally {
                ErrorContext.instance().reset();
            }

        }

        private void closeCursors() {
            if (this.cursorList != null && this.cursorList.size() != 0) {
                Iterator var1 = this.cursorList.iterator();

                while(var1.hasNext()) {
                    Cursor cursor = (Cursor)var1.next();

                    try {
                        cursor.close();
                    } catch (IOException var4) {
                        throw ExceptionFactory.wrapException("Error closing cursor.  Cause: " + var4, var4);
                    }
                }

                this.cursorList.clear();
            }

        }

        public Configuration getConfiguration() {
            return this.configuration;
        }

        public <T> T getMapper(Class<T> type) {
            return this.configuration.getMapper(type, this);
        }

        public Connection getConnection() {
            try {
                return this.executor.getTransaction().getConnection();
            } catch (SQLException var2) {
                throw ExceptionFactory.wrapException("Error getting a new connection.  Cause: " + var2, var2);
            }
        }

        public void clearCache() {
            this.executor.clearLocalCache();
        }

        private <T> void registerCursor(Cursor<T> cursor) {
            if (this.cursorList == null) {
                this.cursorList = new ArrayList();
            }

            this.cursorList.add(cursor);
        }

        private boolean isCommitOrRollbackRequired(boolean force) {
            return !this.autoCommit && this.dirty || force;
        }
        // 集合类型转换
        private Object wrapCollection(Object object) {
            DefaultSqlSession.StrictMap map;
            // 如果是Collection类型参数名称为collection
            if (object instanceof Collection) {
                map = new DefaultSqlSession.StrictMap();
                map.put("collection", object);
                // 如果是List类型，参数名称为list
                if (object instanceof List) {
                    map.put("list", object);
                }

                return map;
                // 如果是Array类型，参数名称为array
            } else if (object != null && object.getClass().isArray()) {
                map = new DefaultSqlSession.StrictMap();
                map.put("array", object);
                return map;
            } else {
                return object;
            }
        }

        public static class StrictMap<V> extends HashMap<String, V> {
            private static final long serialVersionUID = -5741767162221585340L;

            public StrictMap() {
            }

            public V get(Object key) {
                if (!super.containsKey(key)) {
                    throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + this.keySet());
                } else {
                    return super.get(key);
                }
            }
        }
    }


    //
    // Source code recreated from a .class file by IntelliJ IDEA
    // (powered by Fernflower decompiler)
    //

    package org.apache.ibatis.mapping;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.Iterator;
    import java.util.List;
    import org.apache.ibatis.cache.Cache;
    import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
    import org.apache.ibatis.executor.keygen.KeyGenerator;
    import org.apache.ibatis.executor.keygen.NoKeyGenerator;
    import org.apache.ibatis.logging.Log;
    import org.apache.ibatis.logging.LogFactory;
    import org.apache.ibatis.scripting.LanguageDriver;
    import org.apache.ibatis.session.Configuration;

    public final class MappedStatement {
        private String resource;
        private Configuration configuration;
        private String id;
        private Integer fetchSize;
        private Integer timeout;
        private StatementType statementType;
        private ResultSetType resultSetType;
        private SqlSource sqlSource;
        private Cache cache;
        private ParameterMap parameterMap;
        private List<ResultMap> resultMaps;
        private boolean flushCacheRequired;
        private boolean useCache;
        private boolean resultOrdered;
        private SqlCommandType sqlCommandType;
        private KeyGenerator keyGenerator;
        private String[] keyProperties;
        private String[] keyColumns;
        private boolean hasNestedResultMaps;
        private String databaseId;
        private Log statementLog;
        private LanguageDriver lang;
        private String[] resultSets;

        MappedStatement() {
        }

        public KeyGenerator getKeyGenerator() {
            return this.keyGenerator;
        }

        public SqlCommandType getSqlCommandType() {
            return this.sqlCommandType;
        }

        public String getResource() {
            return this.resource;
        }

        public Configuration getConfiguration() {
            return this.configuration;
        }

        public String getId() {
            return this.id;
        }

        public boolean hasNestedResultMaps() {
            return this.hasNestedResultMaps;
        }

        public Integer getFetchSize() {
            return this.fetchSize;
        }

        public Integer getTimeout() {
            return this.timeout;
        }

        public StatementType getStatementType() {
            return this.statementType;
        }

        public ResultSetType getResultSetType() {
            return this.resultSetType;
        }

        public SqlSource getSqlSource() {
            return this.sqlSource;
        }

        public ParameterMap getParameterMap() {
            return this.parameterMap;
        }

        public List<ResultMap> getResultMaps() {
            return this.resultMaps;
        }

        public Cache getCache() {
            return this.cache;
        }

        public boolean isFlushCacheRequired() {
            return this.flushCacheRequired;
        }

        public boolean isUseCache() {
            return this.useCache;
        }

        public boolean isResultOrdered() {
            return this.resultOrdered;
        }

        public String getDatabaseId() {
            return this.databaseId;
        }

        public String[] getKeyProperties() {
            return this.keyProperties;
        }

        public String[] getKeyColumns() {
            return this.keyColumns;
        }

        public Log getStatementLog() {
            return this.statementLog;
        }

        public LanguageDriver getLang() {
            return this.lang;
        }

        public String[] getResultSets() {
            return this.resultSets;
        }

        /** @deprecated */
        @Deprecated
        public String[] getResulSets() {
            return this.resultSets;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            BoundSql boundSql = this.sqlSource.getBoundSql(parameterObject);
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            if (parameterMappings == null || parameterMappings.isEmpty()) {
                boundSql = new BoundSql(this.configuration, boundSql.getSql(), this.parameterMap.getParameterMappings(), parameterObject);
            }

            Iterator var4 = boundSql.getParameterMappings().iterator();

            while(var4.hasNext()) {
                ParameterMapping pm = (ParameterMapping)var4.next();
                String rmId = pm.getResultMapId();
                if (rmId != null) {
                    ResultMap rm = this.configuration.getResultMap(rmId);
                    if (rm != null) {
                        this.hasNestedResultMaps |= rm.hasNestedResultMaps();
                    }
                }
            }
            // 包含了要执行的sql和参数
            return boundSql;
        }

        private static String[] delimitedStringToArray(String in) {
            return in != null && in.trim().length() != 0 ? in.split(",") : null;
        }

        public static class Builder {
            private MappedStatement mappedStatement = new MappedStatement();

            public Builder(Configuration configuration, String id, SqlSource sqlSource, SqlCommandType sqlCommandType) {
                this.mappedStatement.configuration = configuration;
                this.mappedStatement.id = id;
                this.mappedStatement.sqlSource = sqlSource;
                this.mappedStatement.statementType = StatementType.PREPARED;
                this.mappedStatement.resultSetType = ResultSetType.DEFAULT;
                this.mappedStatement.parameterMap = (new org.apache.ibatis.mapping.ParameterMap.Builder(configuration, "defaultParameterMap", (Class)null, new ArrayList())).build();
                this.mappedStatement.resultMaps = new ArrayList();
                this.mappedStatement.sqlCommandType = sqlCommandType;
                this.mappedStatement.keyGenerator = (KeyGenerator)(configuration.isUseGeneratedKeys() && SqlCommandType.INSERT.equals(sqlCommandType) ? Jdbc3KeyGenerator.INSTANCE : NoKeyGenerator.INSTANCE);
                String logId = id;
                if (configuration.getLogPrefix() != null) {
                    logId = configuration.getLogPrefix() + id;
                }

                this.mappedStatement.statementLog = LogFactory.getLog(logId);
                this.mappedStatement.lang = configuration.getDefaultScriptingLanguageInstance();
            }

            public MappedStatement.Builder resource(String resource) {
                this.mappedStatement.resource = resource;
                return this;
            }

            public String id() {
                return this.mappedStatement.id;
            }

            public MappedStatement.Builder parameterMap(ParameterMap parameterMap) {
                this.mappedStatement.parameterMap = parameterMap;
                return this;
            }

            public MappedStatement.Builder resultMaps(List<ResultMap> resultMaps) {
                this.mappedStatement.resultMaps = resultMaps;
                Iterator var2 = resultMaps.iterator();

                while(var2.hasNext()) {
                    ResultMap resultMap = (ResultMap)var2.next();
                    this.mappedStatement.hasNestedResultMaps = this.mappedStatement.hasNestedResultMaps || resultMap.hasNestedResultMaps();
                }

                return this;
            }

            public MappedStatement.Builder fetchSize(Integer fetchSize) {
                this.mappedStatement.fetchSize = fetchSize;
                return this;
            }

            public MappedStatement.Builder timeout(Integer timeout) {
                this.mappedStatement.timeout = timeout;
                return this;
            }

            public MappedStatement.Builder statementType(StatementType statementType) {
                this.mappedStatement.statementType = statementType;
                return this;
            }

            public MappedStatement.Builder resultSetType(ResultSetType resultSetType) {
                this.mappedStatement.resultSetType = resultSetType == null ? ResultSetType.DEFAULT : resultSetType;
                return this;
            }

            public MappedStatement.Builder cache(Cache cache) {
                this.mappedStatement.cache = cache;
                return this;
            }

            public MappedStatement.Builder flushCacheRequired(boolean flushCacheRequired) {
                this.mappedStatement.flushCacheRequired = flushCacheRequired;
                return this;
            }

            public MappedStatement.Builder useCache(boolean useCache) {
                this.mappedStatement.useCache = useCache;
                return this;
            }

            public MappedStatement.Builder resultOrdered(boolean resultOrdered) {
                this.mappedStatement.resultOrdered = resultOrdered;
                return this;
            }

            public MappedStatement.Builder keyGenerator(KeyGenerator keyGenerator) {
                this.mappedStatement.keyGenerator = keyGenerator;
                return this;
            }

            public MappedStatement.Builder keyProperty(String keyProperty) {
                this.mappedStatement.keyProperties = MappedStatement.delimitedStringToArray(keyProperty);
                return this;
            }

            public MappedStatement.Builder keyColumn(String keyColumn) {
                this.mappedStatement.keyColumns = MappedStatement.delimitedStringToArray(keyColumn);
                return this;
            }

            public MappedStatement.Builder databaseId(String databaseId) {
                this.mappedStatement.databaseId = databaseId;
                return this;
            }

            public MappedStatement.Builder lang(LanguageDriver driver) {
                this.mappedStatement.lang = driver;
                return this;
            }

            public MappedStatement.Builder resultSets(String resultSet) {
                this.mappedStatement.resultSets = MappedStatement.delimitedStringToArray(resultSet);
                return this;
            }

            /** @deprecated */
            @Deprecated
            public MappedStatement.Builder resulSets(String resultSet) {
                this.mappedStatement.resultSets = MappedStatement.delimitedStringToArray(resultSet);
                return this;
            }

            public MappedStatement build() {
                assert this.mappedStatement.configuration != null;

                assert this.mappedStatement.id != null;

                assert this.mappedStatement.sqlSource != null;

                assert this.mappedStatement.lang != null;

                this.mappedStatement.resultMaps = Collections.unmodifiableList(this.mappedStatement.resultMaps);
                return this.mappedStatement;
            }
        }
    }
    SimpleExecutor类方法
    public <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        Statement stmt = null;

        List var9;
        try {
            Configuration configuration = ms.getConfiguration();
            // 构建StatementHandler
            StatementHandler handler = configuration.newStatementHandler(this.wrapper, ms, parameter, rowBounds, resultHandler, boundSql);
            // 编译sql，对参数初始化
            stmt = this.prepareStatement(handler, ms.getStatementLog());
            // 执行具体的查询
            var9 = handler.query(stmt, resultHandler);
        } finally {
            this.closeStatement(stmt);
        }

        return var9;
    }

    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        StatementHandler statementHandler = (StatementHandler)this.interceptorChain.pluginAll(statementHandler);
        return statementHandler;
    }


SqlSession下的四大对象：
Executor：代表执行器，由它来调度StatementHandler、ParameterHandler、ResultHandler等来执行对应的SQL。
StatementHandler：使用数据库的Statement(PreparedStatement)执行操作。
ParameterHandler：用于SQL对参数的处理
ResultHandler：进行最后数据集(ResultSet)的封装返回处理
