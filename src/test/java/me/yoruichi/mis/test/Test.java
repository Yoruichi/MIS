package me.yoruichi.mis.test;

import com.google.common.collect.Lists;
import me.yoruichi.mis.Application;
import me.yoruichi.mis.dao.FooDao;
import me.yoruichi.mis.po.Foo;
import me.yoruichi.mis.po.Gender;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        foo.setName("testA").setAge(20).setcTime(LocalDateTime.now());
        try {
            //test insert
            System.out.println("test insert. Will insert 3 records.");
            fooDao.insertOne(foo);
            foo.setcTime(null);
            fooDao.insertOne(foo);
            long id = fooDao.insertOneGetLongId(foo);
            Assert.assertEquals(3, fooDao.selectMany(new Foo().setName("testA")).size());
            //test insert or update
            System.out.println("test insert or update. Will update records with id 3.");
            foo.setId(id);
            foo.setAge(27);
            foo.setName("testB");
            foo.setGender(Gender.M);
            fooDao.insertOrUpdate(foo);
            //test select
            System.out.println("test select. Query records with name in {'testA', 'testB'}");
            Foo f = new Foo();
            f.in("name", new String[] {"testA", "testB"});
            Assert.assertEquals(3, fooDao.selectMany(f).size());

            System.out.println("test select. Query records with age > 22");
            Foo f1 = new Foo();
            f1.gt("age", 22);
            Assert.assertEquals("testB", fooDao.select(f1).getName());

            System.out.println("test select. Query records with ctime > yesterday");
            Foo f0 = new Foo();
            f0.gt("cTime", LocalDate.now().minusDays(1));
            Assert.assertEquals("testA", fooDao.select(f0).getName());

            System.out.println("test select. Query records with age in (20, 22)");
            Foo f2 = new Foo();
            f2.in("age", new Integer[] {20, 22});
            Assert.assertEquals(2, fooDao.selectMany(f2).size());

            System.out.println("test select. Query records with name equals 'testB' or age > 22 or age in (20, 22)");
            Assert.assertEquals(3, fooDao.selectMany(new Foo().setName("testB").or(f1).or(f2)).size());

            //test cache
            System.out.println("test cache");
            Foo f3 = new Foo();
            f3.gte("age", 20).lt("age", 30);
            Assert.assertEquals(3, fooDao.selectMany(f3).size());
            Assert.assertEquals(3, fooDao.selectMany(f3.withCache()).size());
            Assert.assertEquals(3, fooDao.selectMany(f3.withCache()).size());
            fooDao.flushCache();
            Assert.assertEquals(3, fooDao.selectMany(f3.withCache()).size());
            Assert.assertEquals(20, fooDao.select(f3.withCache()).getAge().intValue());
            Assert.assertEquals(20, fooDao.select(f3.withCache()).getAge().intValue());
            Assert.assertEquals(null, fooDao.select(new Foo().setAge(1).withCache()));

            //test update
            System.out.println("test update email where age > 20 or (age < 25 and gender = 0)");
            Foo records = new Foo().update("email", "whatever@google.com").gt("age", 20)
                    .or(new Foo().lt("age", 25).and(new Foo().setGender(Gender.F)));
            fooDao.updateOne(records);
            System.out.println("test update gender = 1 where age = 27 and update gender=0 where age = 22");
            Foo ff = new Foo().setAge(27).update("gender", Gender.F);
            Foo ff1 = new Foo().setAge(20).update("gender", Gender.M);
            fooDao.updateMany(Lists.newArrayList(ff, ff1));
            Assert.assertEquals(Gender.M, fooDao.select(ff1).getGender());

            System.out.println("test select records where gender = 1 order by id asc");
            foo = new Foo();
            foo.setGender(Gender.F).orderBy("id").setAsc();
            List<Foo> fl = fooDao.selectMany(foo);
            fl.stream().forEach(foo1 -> foo1.setGender(Gender.M));
            System.out.println("update set gender = 0 for all records");
            fooDao.insertOrUpdateMany(fl);
            foo.setGender(Gender.M);
            Assert.assertEquals(3, fooDao.selectMany(foo).size());

            System.out.println("test select records where email like %@google.com");
            List<Foo> fll = fooDao.selectMany(new Foo().like("email", "%@google.com"));
            Assert.assertEquals(3, fll.size());

            //test custom method
            fooDao.selectFooCustom(foo);
            fooDao.doMethod(fooDao::selectFooCustom, foo.withCache(), foo.toString());
            fooDao.doMethod(fooDao::selectFooCustom, foo.withCache(), foo.toString());
            //test and/or
            System.out.println("test for and/or");
            Assert.assertEquals(3,
                    fooDao.selectMany(new Foo().setAge(20).or(new Foo().setAge(27))).size());
            Assert.assertEquals(0,
                    fooDao.selectMany(new Foo().setAge(22).and(new Foo().setAge(27))).size());
            Assert.assertEquals(2, fooDao.selectMany(
                    new Foo().setAge(20).and(new Foo().setAge(27).or(new Foo().gt("age", 0))))
                    .size());

            System.out.println("test for count");
            Long size = fooDao.selectCount(new Foo());
            Assert.assertEquals(3L, size.longValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
