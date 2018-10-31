package org.projectodd.openwhisk.tests;

import org.projectodd.openwhisk.QualifiedName;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class QualifiedNameTest {
    @DataProvider(name = "names")
    public Object[][] names() {
        return new Object[][] {
            new Object[] {"foo", "_", "foo", null, "foo"},
            new Object[] {"pkg/foo", "_", "pkg/foo", "pkg", "foo"},
            new Object[] {"/ns/foo", "ns", "foo", null, "foo"},
            new Object[] {"/ns/pkg/foo", "ns", "pkg/foo", "pkg", "foo"}
        };
    }

    @Test(dataProvider = "names")
    public void namespaces(String name, String nameSpace, String entityName, String packageName, String entity) {
        final QualifiedName qualifiedName = QualifiedName.Companion.qualifiedName(name);
        Assert.assertEquals(qualifiedName.getNamespace(), nameSpace);
        Assert.assertEquals(qualifiedName.getEntityName(), entityName);
        Assert.assertEquals(qualifiedName.getPackageName(), packageName);
        Assert.assertEquals(qualifiedName.getEntity(), entity);
    }
}
