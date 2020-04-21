package sudokudbj.model;

import java.util.ArrayList;

public class MdlSudoku {
    private ArrayList<MdlSudokuRegion> rows = new ArrayList<MdlSudokuRegion>(9);
    private ArrayList<MdlSudokuRegion> cols = new ArrayList<MdlSudokuRegion>(9);
    private ArrayList<MdlSudokuRegion> boxs = new ArrayList<MdlSudokuRegion>(9);

    private MdlSudokuCell[] cells = new MdlSudokuCell[81];

    private ArrayList<MdlSudokuCell> done = new ArrayList<MdlSudokuCell>();

    public MdlSudoku(int[] in) {
        for (int i = 0; i < 9; i ++) {
            this.rows.add(i, new MdlSudokuRegion("第" + (i + 1) + "行"));
            this.cols.add(i, new MdlSudokuRegion("第" + (i + 1) + "列"));
            this.boxs.add(i, new MdlSudokuRegion("第" + (i + 1) + "宫"));
        }

        for (int i = 0; i < 81; i ++) {
            int row = i / 9;
            int col = i % 9;

            int region = -1;
            if (i ==  0 || i ==  1 || i ==  2 || i ==  9 || i == 10 || i == 11 || i == 18 || i == 19 || i == 20 ) { region = 0; }
            if (i ==  3 || i ==  4 || i ==  5 || i == 12 || i == 13 || i == 14 || i == 21 || i == 22 || i == 23 ) { region = 1; }
            if (i ==  6 || i ==  7 || i ==  8 || i == 15 || i == 16 || i == 17 || i == 24 || i == 25 || i == 26 ) { region = 2; }
            if (i == 27 || i == 28 || i == 29 || i == 36 || i == 37 || i == 38 || i == 45 || i == 46 || i == 47 ) { region = 3; }
            if (i == 30 || i == 31 || i == 32 || i == 39 || i == 40 || i == 41 || i == 48 || i == 49 || i == 50 ) { region = 4; }
            if (i == 33 || i == 34 || i == 35 || i == 42 || i == 43 || i == 44 || i == 51 || i == 52 || i == 53 ) { region = 5; }
            if (i == 54 || i == 55 || i == 56 || i == 63 || i == 64 || i == 65 || i == 72 || i == 73 || i == 74 ) { region = 6; }
            if (i == 57 || i == 58 || i == 59 || i == 66 || i == 67 || i == 68 || i == 75 || i == 76 || i == 77 ) { region = 7; }
            if (i == 60 || i == 61 || i == 62 || i == 69 || i == 70 || i == 71 || i == 78 || i == 79 || i == 80 ) { region = 8; }

            MdlSudokuRegion regionRow = this.rows.get(row);
            MdlSudokuRegion regionCol = this.cols.get(col);
            MdlSudokuRegion regionBox = this.boxs.get(region);

            this.cells[i] = new MdlSudokuCell(i, in[i], regionCol, regionRow, regionBox);
            if (in[i] != 0) {
                this.done.add(this.cells[i]);
            }
            regionRow.add(this.cells[i]);
            regionCol.add(this.cells[i]);
            regionBox.add(this.cells[i]);
        } // for

        // 根据提示信息整理剩余空白格可能出现的数字。
        for (MdlSudokuCell cell : this.done) {
            cell.proc(0, null);
        }
    }

    public MdlSudoku(MdlSudoku sdk) {
        for (int i = 0; i < 9; i ++) {
            this.rows.add(i, new MdlSudokuRegion("第" + (i + 1) + "行"));
            this.cols.add(i, new MdlSudokuRegion("第" + (i + 1) + "列"));
            this.boxs.add(i, new MdlSudokuRegion("第" + (i + 1) + "宫"));
        }

        MdlSudokuCell[] cels = sdk.getCells();
        for (int i = 0; i < 81; i ++) {
            int row = i / 9;
            int col = i % 9;

            int region = -1;
            if (i ==  0 || i ==  1 || i ==  2 || i ==  9 || i == 10 || i == 11 || i == 18 || i == 19 || i == 20 ) { region = 0; }
            if (i ==  3 || i ==  4 || i ==  5 || i == 12 || i == 13 || i == 14 || i == 21 || i == 22 || i == 23 ) { region = 1; }
            if (i ==  6 || i ==  7 || i ==  8 || i == 15 || i == 16 || i == 17 || i == 24 || i == 25 || i == 26 ) { region = 2; }
            if (i == 27 || i == 28 || i == 29 || i == 36 || i == 37 || i == 38 || i == 45 || i == 46 || i == 47 ) { region = 3; }
            if (i == 30 || i == 31 || i == 32 || i == 39 || i == 40 || i == 41 || i == 48 || i == 49 || i == 50 ) { region = 4; }
            if (i == 33 || i == 34 || i == 35 || i == 42 || i == 43 || i == 44 || i == 51 || i == 52 || i == 53 ) { region = 5; }
            if (i == 54 || i == 55 || i == 56 || i == 63 || i == 64 || i == 65 || i == 72 || i == 73 || i == 74 ) { region = 6; }
            if (i == 57 || i == 58 || i == 59 || i == 66 || i == 67 || i == 68 || i == 75 || i == 76 || i == 77 ) { region = 7; }
            if (i == 60 || i == 61 || i == 62 || i == 69 || i == 70 || i == 71 || i == 78 || i == 79 || i == 80 ) { region = 8; }

            MdlSudokuRegion regionRow = this.rows.get(row);
            MdlSudokuRegion regionCol = this.cols.get(col);
            MdlSudokuRegion regionBox = this.boxs.get(region);

            this.cells[i] = new MdlSudokuCell(i, cels[i].getValue(), regionCol, regionRow, regionBox);
            if (cels[i].getValue() != 0) {
                this.done.add(this.cells[i]);
            }
            regionRow.add(this.cells[i]);
            regionCol.add(this.cells[i]);
            regionBox.add(this.cells[i]);
        } // for

        // 整理信息
        for (MdlSudokuCell cell : this.done) {
            cell.proc(0, null);
        }
    }

    public boolean isOK() {
        boolean result = true;
        for (MdlSudokuCell cell : this.cells) {
            if (cell.getValue() == 0) {
                result = false;
                break ;
            }
        }
        return result;
    }

    public String getText() {
        StringBuilder buf = new StringBuilder();
        int j = 0;
        for(MdlSudokuCell v : cells) {
            buf .append(v);
             j ++;
             if (j % 9 == 0) {
                 buf .append("\n");
             } else {
                 buf .append(",");
             }
         }// for
        return buf.toString();
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        int j = 0;
        for(MdlSudokuCell v : cells) {
           buf .append(v);
            j ++;
            if (j % 9 == 0) {
                buf .append("\n");
            } else {
                buf .append(",");
            }
        }// for
        buf .append("行:\n");
        for (MdlSudokuRegion regionRow : this.rows) {
            buf.append(regionRow);
            buf .append("\n");
        }
        buf .append("列:\n");
        for (MdlSudokuRegion regionCol : this.cols) {
            buf.append(regionCol);
            buf .append("\n");
        }
        buf .append("宫:\n");
        for (MdlSudokuRegion regionBox : this.boxs) {
            buf.append(regionBox);
            buf .append("\n");
        }
        buf.append("done:" + this.done.size());

        return buf.toString();
    }

    /**
     * @return rows
     */
    public ArrayList<MdlSudokuRegion> getRows() {
      return rows;
    }

    /**
     * @param rows  rows
     */
    public void setRows(ArrayList<MdlSudokuRegion> rows) {
      this.rows = rows;
    }

    /**
     * @return cols
     */
    public ArrayList<MdlSudokuRegion> getCols() {
      return cols;
    }

    /**
     * @param cols  cols
     */
    public void setCols(ArrayList<MdlSudokuRegion> cols) {
      this.cols = cols;
    }

    /**
     * @return boxs
     */
    public ArrayList<MdlSudokuRegion> getBoxs() {
      return boxs;
    }

    /**
     * @param boxs  boxs
     */
    public void setBoxs(ArrayList<MdlSudokuRegion> boxs) {
      this.boxs = boxs;
    }

    /**
     * @return cells
     */
    public MdlSudokuCell[] getCells() {
      return cells;
    }

    /**
     * @param cells  cells
     */
    public void setCells(MdlSudokuCell[] cells) {
      this.cells = cells;
    }

    /**
     * @return done
     */
    public ArrayList<MdlSudokuCell> getDone() {
      return done;
    }

    /**
     * @param done  done
     */
    public void setDone(ArrayList<MdlSudokuCell> done) {
      this.done = done;
    }
}
