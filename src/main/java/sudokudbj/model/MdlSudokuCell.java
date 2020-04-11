package sudokudbj.model;

import java.util.ArrayList;
import java.util.HashMap;

public class MdlSudokuCell {
    private int index;
    private int value;
    private HashMap<Integer, Integer> maybe = new HashMap<Integer, Integer>();

    private MdlSudokuRegion row;
    private MdlSudokuRegion col;
    private MdlSudokuRegion region;

    /**
     * 仅在ProcSudoku中被调用
     * @param idx
     * @param val
     * @param col
     * @param row
     * @param region
     */
    public MdlSudokuCell(int idx, int val, MdlSudokuRegion col, MdlSudokuRegion row, MdlSudokuRegion region) {
        this.index = idx;
        this.row = row;
        this.col = col;
        this.region = region;

        if (val == 0) {
            this.value = 0;
            this.maybe.put(1, 1);
            this.maybe.put(2, 2);
            this.maybe.put(3, 3);
            this.maybe.put(4, 4);
            this.maybe.put(5, 5);
            this.maybe.put(6, 6);
            this.maybe.put(7, 7);
            this.maybe.put(8, 8);
            this.maybe.put(9, 9);
        } else {
            this.value = val;
            this.maybe.clear();
            /*
            // 问题：之前已经创建的单元格OK
            //      之后创建的单元格NG，每次创建全部追溯有点低效。
            //      改为统一追溯，参照proc,ProcSudoku
            this.row.remove(val, this);
            this.col.remove(val, this);
            this.region.remove(val, this);
            */
        }
    }

    public void proc(int debug, String msg) {
        if (this.value != 0) {
            this.row.remove(this.value, this, debug, msg);
            this.col.remove(this.value, this, debug, msg);
            this.region.remove(this.value, this, debug, msg);
        }
    }

    public void setValue(int val) {
        this.setValue(val, null);
    }
    public void setValue(int val, String msg) {
        this.value = val;
        if (this.value != 0) {
            this.maybe.clear();
            this.proc(0, msg);
        }
    }

    public int getRemainderCount() {
        return this.maybe.keySet().size();
    }
    public String getRemainderString() {
        StringBuffer buf = new StringBuffer();
        for (Integer b : this.maybe.keySet()) {
            buf.append(b);
        }
        return buf.toString();
    }

    public void remove(int key, int debug, String msg) {
        if (this.value != 0) {
            return ;
        }
        if (debug == 1) System.out.println(msg + "从对象" + this + "中删除可能值:" + key);
        this.maybe.remove(key);
    }

    public boolean nakedSingle() {
        long startTime=System.currentTimeMillis();   //获取开始时间
        long startTimeN =System.nanoTime();   //获取开始时间
        // System.out.println("★余数法nakedSingle------");

        boolean result = false;
        ArrayList<Integer> set = new ArrayList<Integer>(9);
        set.add(1);set.add(2);set.add(3);
        set.add(4);set.add(5);set.add(6);
        set.add(7);set.add(8);set.add(9);

        /*
         * 唯一解法
         * 当某行已填数字的宫格达到8个，那么该行剩余宫格能填的数字就只剩下那个还没出现过的数字了。成为行唯一解。
         */
        for (Integer k : this.row.getDone().keySet()) {
            set.remove(k);
        }
        for (Integer k : this.col.getDone().keySet()) {
            set.remove(k);
        }
        /*
         * 唯余解法
         * 唯余解法就是某宫格可以添入的数已经排除了8个,那么这个宫格的数字就只能添入那个没有出现的数字。
         */
        for (Integer k : this.region.getDone().keySet()) {
            set.remove(k);
        }
        if (set.size() == 1) {
            int key = set.get(0);
            // this.value must is zero.
            this.remove(key, 1, "★余数法");
            this.value = key;
            this.proc(0, "★余数法");
            result = true;
        }

        long endTimeN=System.nanoTime(); //获取结束时间
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("★余数法nakedSingle程序运行时间： " + (endTime - startTime) + "ms" + (endTimeN - startTimeN) + "ns" + this);
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((col == null) ? 0 : col.hashCode());
        result = prime * result + index;
        result = prime * result + ((maybe == null) ? 0 : maybe.hashCode());
        result = prime * result + ((region == null) ? 0 : region.hashCode());
        result = prime * result + ((row == null) ? 0 : row.hashCode());
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof MdlSudokuCell) {
            MdlSudokuCell another = (MdlSudokuCell)anObject;
            return this.getRemainderString().equals(another.getRemainderString());
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        if (this.index < 10) buf.append("0");
        buf.append(this.index);
        buf.append("]");
        if (value != 0) buf.append(this.value);
        if ( this.value == 0 ) {
            buf.append("{");
            if (!this.maybe.isEmpty()) {
                Object[] keys = this.maybe.keySet().toArray();
                int max = keys.length - 1;
                for (int i = 0; i <= max; i ++) {
                    Object key = keys[i];
                    buf.append(key);
                    if (i  != max) {
                        buf.append(".");
                    }
                }
            } else {
                buf.append("●");
            }
            buf.append("}");
        }
        return buf.toString();
    }

    public int getIndex() {
      return index;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public HashMap<Integer, Integer> getMaybe() {
      return maybe;
    }

    public void setMaybe(HashMap<Integer, Integer> maybe) {
      this.maybe = maybe;
    }

    public MdlSudokuRegion getRow() {
      return row;
    }

    public void setRow(MdlSudokuRegion row) {
      this.row = row;
    }

    public MdlSudokuRegion getCol() {
      return col;
    }

    public void setCol(MdlSudokuRegion col) {
      this.col = col;
    }

    public MdlSudokuRegion getRegion() {
      return region;
    }

    public void setRegion(MdlSudokuRegion region) {
      this.region = region;
    }

    public int getValue() {
      return value;
    }
}
