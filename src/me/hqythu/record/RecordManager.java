package me.hqythu.record;

import me.hqythu.utils.Condition;

import java.io.ByteArrayInputStream;
import java.util.Scanner;


//recordLength 记录的长度
//recordSizePerPage 每一页记录的个数
//size         文件中记录总数
//fieldSize    记录的字段个数
//recordFieldPos 记录的每个字段的位置
public class RecordManager {

    public static final int PER_PAGE_INFO = 96;
    public static final int PER_PAGE_DATA = 8096;

    int recordLength;
    int recordSizePerPage;
    int size;
    int fieldSize;
    int[] recordFieldPos;

    public void insert(Record record) {

    }

    public void remove(Condition condition) {

    }

    public void update(Record record, Condition condition) {

    }

    public Record[] query(Condition condition) {
        return null;
    }

    public RecordManager(int fileId) throws Exception {
        byte[] firstPage = FilePageManager.getInstance().readPage(fileId, 0);
        if (firstPage == null) throw new Exception();

        Scanner scanner = new Scanner(new ByteArrayInputStream(firstPage));
        recordLength = scanner.nextInt();
        recordSizePerPage = scanner.nextInt();
        size = scanner.nextInt();
        fieldSize = scanner.nextInt();
        recordFieldPos = new int[fieldSize];
        for (int i = 0; i < fieldSize; i++) {
            recordFieldPos[i] = scanner.nextInt();
        }
    }
}

