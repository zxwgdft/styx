package com.paladin.monitor.core.security;

import com.paladin.monitor.model.org.OrgPermission;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Menu implements Serializable {

    private String id;

    private String url;

    private String name;

    private String icon;

    private boolean owned;

    private boolean isRoot;

    private int order;

    private List<Menu> children;

    public Menu(){

    }

    public Menu(Permission permission, boolean owned) {
        this.id = permission.getId();
        OrgPermission source = permission.getSource();
        this.url = source.getUrl();
        this.name = source.getName();
        this.icon = source.getMenuIcon();
        this.order = source.getListOrder();
        this.owned = owned;
        this.isRoot = permission.isRootMenu();
        this.children = new ArrayList<>();
    }


}
