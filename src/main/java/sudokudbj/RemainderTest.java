package sudokudbj;

import java.util.concurrent.Callable;

import sudokudbj.model.MdlSudoku;

public class RemainderTest implements Callable<Integer> {
    private MdlSudoku mdl = null;
    private ProcSudoku proc = null;

    public RemainderTest(int offset, int value, MdlSudoku mdl) {
        this.mdl = mdl;
        System.out.println("余数测试a[" + offset + "]" + value + "\n" + this.mdl);
        this.mdl.getCells()[offset].setValue(value, "余数测试");
        System.out.println("余数测试b[" + offset + "]" + value + "\n" + this.mdl);
        proc = new ProcSudoku(0);
    }

    @Override
    public Integer call() throws Exception {
        return this.proc.proc(this.mdl) ? 1 : 0;
    }

}
