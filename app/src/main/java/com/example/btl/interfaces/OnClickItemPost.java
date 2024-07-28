package com.example.btl.interfaces;

import com.example.btl.entites.PostEntity;

public interface OnClickItemPost {
    void onClickItemDelete(String id);
    void onClickItemEdit(PostEntity post);
}
