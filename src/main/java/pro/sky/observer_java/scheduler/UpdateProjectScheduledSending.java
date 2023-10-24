package pro.sky.observer_java.scheduler;

import com.intellij.ide.actions.SaveAllAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import pro.sky.observer_java.constants.CustomSocketEvents;
import pro.sky.observer_java.fileProcessor.FileStructureStringer;
import pro.sky.observer_java.model.ProjectFile;
import pro.sky.observer_java.resources.ResourceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdateProjectScheduledSending implements Runnable {
    private final ResourceManager resourceManager;
    private final Project project;
    public UpdateProjectScheduledSending(ResourceManager resourceManager, Project project) {
        this.resourceManager = resourceManager;
        this.project = project;
    }

    @Override
    public void run() {
        List<ProjectFile> updatedFiles = resourceManager.getEditorUpdateEvents();
        VirtualFile apiDir = project.getBaseDir();
        VfsUtil.markDirtyAndRefresh(true, true, true, apiDir);
        updatedFiles.removeAll(Collections.singleton(null));
       // FileStructureStringer fileStructureStringer = new FileStructureStringer(resourceManager);
        List<VirtualFile> fileList = VfsUtil.collectChildrenRecursively(apiDir);

        VirtualFile[] fileArray = fileList.toArray(new VirtualFile[fileList.size()]);
      //  VirtualFile[] vFilesArray = apiDir.getChildren();
        SaveAllAction saveAllAction = new SaveAllAction();
        // saveAllAction.actionPerformed(new AnActionEvent());

        if (updatedFiles.isEmpty()) {

            // apiDir.refresh(false,true);
            ApplicationManager.getApplication().invokeLater(() -> {
                FileDocumentManager.getInstance().saveAllDocuments();
            });

//            VfsUtil.markDirtyAndRefresh(
//                    false,
//                    true,
//                    true,
//                    fileArray
//            );


            return;
        }

        FileStructureStringer stringer = new FileStructureStringer(resourceManager);
        String json = stringer.getJsonStringFromProjectFileList(updatedFiles);

        resourceManager.getmSocket().emit(CustomSocketEvents.CODE_UPDATE, stringer.getJsonObjectFromString(json));
        resourceManager.setEditorUpdateEvents(new ArrayList<>());

    }
}
