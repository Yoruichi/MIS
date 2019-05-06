package me.yoruichi.mis.test;

import com.google.common.collect.Lists;
import me.yoruichi.mis.Application;
import me.yoruichi.mis.dao.FooDao;
import me.yoruichi.mis.po.Foo;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by yoruichi on 16/10/26.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class Test {

    @Autowired
    private FooDao fooDao;

    @Rollback
    @org.junit.Test
    public void testInjection() {
        Foo foo = new Foo();
        try {
            List<Foo> list = fooDao.selectMany(foo.in("name", new Object[] {"1') or 1=1 or `name` in ('1"}));
//            List<Foo> list = fooDao.selectMany(foo.in("name", new Object[] {"1", "testA"}));
            System.out.println(list);
            fooDao.updateOne(foo.update("age", 26));
            list = fooDao.selectMany(foo.in("name", new Object[] {"1", "testA"}));
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Rollback
    @org.junit.Test
    public void test() {
        Foo foo = new Foo();
        fooDao.getTemplate().update("delete from foo");
        foo.setName("testA").setAge(22);
        try {
            //test insert
            fooDao.insertOne(foo);
            fooDao.insertOne(foo);
            long id = fooDao.insertOneGetLongId(foo);
            Assert.assertEquals(3, fooDao.selectMany(foo).size());
            foo.setId(id);
            foo.setAge(27);
            foo.setName("testB");
            foo.setGender(false);
            fooDao.insertOrUpdate(foo);
            //test select
            Foo f = new Foo();
            f.in("name", new String[] {"testA", "testB"});
            Assert.assertEquals(3, fooDao.selectMany(f).size());
            Foo f1 = new Foo();
            f1.gt("age", 22);
            Assert.assertEquals("testB", fooDao.select(f1).getName());
            Foo f2 = new Foo();
            f2.in("age", new Integer[] {22, 27});
            Assert.assertEquals(3, fooDao.selectMany(f).size());
            Assert.assertEquals(3, fooDao.selectMany(f.or(f1).or(f2)).size());
            //test cache
            System.out.println("test cache");
            Foo f3 = new Foo();
            f3.gte("age", 22).lt("age", 30);
            Assert.assertEquals(3, fooDao.selectMany(f3).size());
            Assert.assertEquals(3, fooDao.selectMany(f3.withCache()).size());
            Assert.assertEquals(3, fooDao.selectMany(f3.withCache()).size());
            fooDao.flushCache();
            Assert.assertEquals(3, fooDao.selectMany(f3.withCache()).size());
            Assert.assertEquals(22, fooDao.select(f3.withCache()).getAge().intValue());
            Assert.assertEquals(22, fooDao.select(f3.withCache()).getAge().intValue());
            Assert.assertEquals(null, fooDao.select(new Foo().setAge(1).withCache()));
            //test update
            System.out.println("test for update");
            f1.update("email", "whatever@google.com").gt("age", 0)
                    .or(new Foo().lt("age", 30).and(new Foo().setGender(false)));
            fooDao.updateOne(f1);

            Foo ff = new Foo().setAge(27).update("gender", true);
            Foo ff1 = new Foo().setAge(22).update("gender", false);
            fooDao.updateMany(Lists.newArrayList(ff, ff1));
            Assert.assertEquals(false, fooDao.select(ff1).getGender());
            foo = new Foo();
            foo.setGender(true).orderBy("id").setAsc();
            List<Foo> fl = fooDao.selectMany(foo);
            fl.stream().forEach(foo1 -> foo1.setGender(false));
            fooDao.insertOrUpdateMany(fl);
            foo.setGender(false);
            Assert.assertEquals(3, fooDao.selectMany(foo).size());
            List<Foo> fll = fooDao.selectMany(new Foo().like("email", "%@google.com"));
            Assert.assertEquals(3, fll.size());
            //test custom method
            fooDao.selectFooCustom(foo);
            fooDao.doMethod(fooDao::selectFooCustom, foo.withCache(), foo.toString());
            fooDao.doMethod(fooDao::selectFooCustom, foo.withCache(), foo.toString());
            //test and/or
            System.out.println("test for and/or");
            Assert.assertEquals(3,
                    fooDao.selectMany(new Foo().setAge(22).or(new Foo().setAge(27))).size());
            Assert.assertEquals(0,
                    fooDao.selectMany(new Foo().setAge(22).and(new Foo().setAge(27))).size());
            Assert.assertEquals(2, fooDao.selectMany(
                    new Foo().setAge(22).and(new Foo().setAge(27).or(new Foo().gt("age", 0))))
                    .size());
            System.out.println("test for count");
            Long size = fooDao.selectCount(new Foo());
            Assert.assertEquals(3L, size.longValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
