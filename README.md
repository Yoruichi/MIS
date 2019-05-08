# MIS

## Model类定义规范
   1. 类名符合"驼峰"规则，且对应的数据库表名使用"_"做单词分隔符。
      比如类名Foo对应表名foo，类名HighLevelStudent对应表名high_level_student。
   
   2. 类的属性一定为class或包装类。比如int要用Integer来声明成员变量。
   
   3. 类的属性名符合"驼峰"规则，且对应的数据库字段名使用"_"做单词分隔符。
      比如类的成员变量名为id对应字段名为id，成员变量名为chineseScore对应字段名为chinese_score。
      
   4. 可以通过创建`Enum`类型实现`me.yoruichi.GenericType`接口来做Model类的成员变量。可以自动映射到`tinyint`的jdbc类型。
   
   5. 可以通过`@Alias`注解来修饰成员变量，通过成员变量类的指定方法来做关联。

### Generator
   点击[这里](/mis-generator-0.0.1-bundle.tar.gz) 

## Model类使用
   
   1. 继承自`BasePo`类。方法
      `gt`: > 
      
      `gte`: >=
      
      `lt`: <
      
      `lte`: <=
      
      `eq`: =
      
      `in`: in 
      
      `notIn`: not in
      
      `isNull`: is null
      
      `isNotNull`: is not null
      
      `ne`: != / <>
      
      `orderBy`: order by
      

## DAO类的使用
   
   1. 继承自`BaseDao`类。~~实现方法getEntityClass()，返回该DAO的泛型对象class~~
   
   2. 方法包括
      1. insertOne,
      2. insertOneGetId // 返回自增id int型,
      3. insertMany,
      4. insertOrUpdate //insert into on duplicate key update,
      5. select,
      6. selectMany,
   
   3. 自定义SQL执行
      dao.getTemplate()获取到template对象，然后可以执行自定义SQL
   
   4. 提供ResultSetExtractor<List<T>>使用
      在自定义SQL执行结果是多行时，可以使用该对象进行对应的mapping操作。
   
   5. 可以通过实现initFieldProcessingMap来自定义从jdbc类型到java类型的转换。
     
