package xworker.swt.app;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Listener;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;


public class WorkbenchTablFolderListener implements CTabFolder2Listener, MouseListener {
    //private static final String TAG = WorkbenchTablFolderListener.class.getName();

    Workbench workbench;
    int[] lastTopSashFormWeights = null;
    int[] lastMainSashFormWeights = null;

    public WorkbenchTablFolderListener(Workbench workbench){
        this.workbench = workbench;
    }

    public void restoreWeights(){
        if(lastTopSashFormWeights != null){
            workbench.topSashForm.setWeights(lastTopSashFormWeights);
            workbench.mainSashForm.setWeights(lastMainSashFormWeights);

            lastTopSashFormWeights = null;
            lastMainSashFormWeights = null;
        }
    }

    @Override
    public void close(CTabFolderEvent cTabFolderEvent) {
        CTabFolder cTabFolder = (CTabFolder) cTabFolderEvent.getSource();
        if(cTabFolder == workbench.mainTabFolder){
            return;
        }

        if(cTabFolder.getItemCount() == 0){
            Composite parent = cTabFolder.getParent();

            restoreWeights();
            cTabFolder.dispose();
            parent.layout();

        }
        //Executor.info(TAG, "Closed: " + cTabFolderEvent.getSource());
    }

    @Override
    public void minimize(CTabFolderEvent cTabFolderEvent) {

    }

    @Override
    public void maximize(CTabFolderEvent cTabFolderEvent) {

    }

    @Override
    public void restore(CTabFolderEvent cTabFolderEvent) {

    }

    @Override
    public void showList(CTabFolderEvent cTabFolderEvent) {

    }

    @Override
    public void mouseDoubleClick(MouseEvent mouseEvent) {
        if(lastTopSashFormWeights != null){
            workbench.topSashForm.setWeights(lastTopSashFormWeights);
            workbench.mainSashForm.setWeights(lastMainSashFormWeights);

            lastTopSashFormWeights = null;
            lastMainSashFormWeights = null;
        }else {
            if(workbench.leftTabFolder.isDisposed() && workbench.rightTabFolder.isDisposed() && workbench.bottomTabFolder.isDisposed()){
                return;
            }

            lastTopSashFormWeights = workbench.topSashForm.getWeights();
            lastMainSashFormWeights = workbench.mainSashForm.getWeights();

            int[] newTopSashFormWeights = null;
            int[] newMainSashFormWeights = null;


            if(mouseEvent.getSource() == workbench.leftTabFolder){
                if(workbench.rightTabFolder.isDisposed()){
                    newMainSashFormWeights = new int[]{100, 0};
                }else{
                    newMainSashFormWeights = new int[]{100, 0, 0};
                }
                if(!workbench.bottomTabFolder.isDisposed()) {
                    newTopSashFormWeights = new int[]{100, 0};
                }

            }else if(mouseEvent.getSource() == workbench.mainTabFolder){
                if(workbench.leftTabFolder.isDisposed() && workbench.rightTabFolder.isDisposed()){
                    newMainSashFormWeights = new int[]{100};
                }else if(workbench.leftTabFolder.isDisposed() && !workbench.rightTabFolder.isDisposed()){
                    newMainSashFormWeights = new int[]{100, 0};
                }else if(!workbench.leftTabFolder.isDisposed() && workbench.rightTabFolder.isDisposed()){
                    newMainSashFormWeights = new int[]{0, 100};
                }else{
                    newMainSashFormWeights = new int[]{0, 100, 0};
                }

                if(!workbench.bottomTabFolder.isDisposed()) {
                    newTopSashFormWeights = new int[]{100, 0};
                }
            }else if(mouseEvent.getSource() == workbench.rightTabFolder){
                if(workbench.leftTabFolder.isDisposed()){
                    newMainSashFormWeights = new int[]{0, 100};
                }else{
                    newMainSashFormWeights = new int[]{0, 0, 100};
                }
                if(!workbench.bottomTabFolder.isDisposed()) {
                    newTopSashFormWeights = new int[]{100, 0};
                }
            }else if(mouseEvent.getSource() == workbench.bottomTabFolder){
                newTopSashFormWeights = new int[]{0, 100};
                newMainSashFormWeights = workbench.getMainSashForm().getWeights();
            }

            if(newTopSashFormWeights != null){
                workbench.topSashForm.setWeights(newTopSashFormWeights);
            }
            if(newMainSashFormWeights != null){
                workbench.mainSashForm.setWeights(newMainSashFormWeights);
            }
        }

        //Executor.info(TAG, "Mouse double click");
    }

    @Override
    public void mouseDown(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseUp(MouseEvent mouseEvent) {

    }
}
