package com.jidesoft.plaf.basic;

import com.jidesoft.testing.FlatLafTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FlatLafTest
class BasicFolderChooserUITest {
    @Test
    void acceptsRegularFilesAndDirectories(@TempDir Path temporaryDirectory) throws IOException {
        Path file = Files.createFile(temporaryDirectory.resolve("file.txt"));
        Path directory = Files.createDirectory(temporaryDirectory.resolve("directory"));

        assertTrue(BasicFolderChooserUI.isFileSystem(file.toFile()));
        assertTrue(BasicFolderChooserUI.isFileSystem(directory.toFile()));
    }

    @Test
    void rejectsVirtualEntriesAndLinksToDirectories(@TempDir Path temporaryDirectory) throws IOException {
        Path file = Files.createFile(temporaryDirectory.resolve("file.txt"));
        Path directory = Files.createDirectory(temporaryDirectory.resolve("directory"));

        assertFalse(BasicFolderChooserUI.isFileSystem(directory.toFile(), new TestFileSystemView(false, false)));
        assertFalse(BasicFolderChooserUI.isFileSystem(directory.toFile(), new TestFileSystemView(true, true)));
        assertTrue(BasicFolderChooserUI.isFileSystem(directory.toFile(), new TestFileSystemView(true, false)));
        assertTrue(BasicFolderChooserUI.isFileSystem(file.toFile(), new TestFileSystemView(true, true)));
    }

    private static class TestFileSystemView extends FileSystemView {
        private final boolean fileSystem;
        private final boolean link;

        TestFileSystemView(boolean fileSystem, boolean link) {
            this.fileSystem = fileSystem;
            this.link = link;
        }

        @Override
        public boolean isFileSystem(File file) {
            return fileSystem;
        }

        @Override
        public boolean isLink(File file) {
            return link;
        }

        @Override
        public File createNewFolder(File containingDirectory) {
            throw new UnsupportedOperationException();
        }
    }
}
