/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensim.view.dataObjects;

import java.io.IOException;
import java.util.Set;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.nodes.Children;
import org.openide.nodes.Node.Cookie;
import org.openide.util.Lookup;
import org.openide.text.DataEditorSupport;

public class OsimModelDataObject extends MultiDataObject {

    public OsimModelDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        CookieSet cookies = getCookieSet();
        //cookies.add((Node.Cookie) DataEditorSupport.create(this, getPrimaryEntry(), cookies));
        cookies.add((Node.Cookie) new OsimOpenSupport(getPrimaryEntry()));
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup());
    }

    @Override
    public Lookup getLookup() {
        System.out.println(getCookieSet().toString());
        return getCookieSet().getLookup();
    }

    @Override
    public Set<FileObject> files() {
        return super.files();
    }

    @Override
    public <T extends Cookie> T getCookie(Class<T> type) {
        return super.getCookie(type);
    }

    @Override
    public boolean isModified() {
        return super.isModified();
    }
    
}
