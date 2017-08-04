package com.example.fmonasterios.nav_view_expandable_list.Memories_List;

/**
 * Created by fmonasterios on 3/28/2017.
 */

public class Items_Memories {
    private String name,image,date,description;

    public Items_Memories(String name, String image, String date, String description) {
        super();
        this.name = name;
        this.image = image;
        this.date = date;
        this.description = description;

    }

    public String getname() {
        return name;
    }
    public String getimage() {
        return image;
    }
    public String getdate() {
        return date;
    }
    public String getdescription() {
        return description;
    }
}
