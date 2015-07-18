// INotePad.aidl
package com.tct.jrdnote;

// Declare any non-default types here with import statements

interface INotePad
{
    int bindNoteData(int type);

    String getTime();
    String getDBText();
    String getSDCardText();
    long getCurrentPosition();
    long getDataLength();
    boolean isNotesItemNull();
    int getType();

    boolean addNewNote();
    void updateNoteWidget(int flag);
}
