package com.example.testlake;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract.Document;
import android.provider.DocumentsContract.Root;
import android.provider.DocumentsProvider;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.testlake.core.utils.FileUtils;
import com.example.testlake.storage.FileEntity;
import com.example.testlake.storage.FileRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class FileProvider extends DocumentsProvider {

    private static final String TAG = FileProvider.class.getSimpleName();

    // Используются  в качестве столбцов по умолчанию для возврата информации о
    // корневом каталоге, если в запросе не запрашиваются конкретные столбцы
    private static final String[] DEFAULT_ROOT_PROJECTION = new String[] {
            Root.COLUMN_ROOT_ID,
            Root.COLUMN_MIME_TYPES,
            Root.COLUMN_FLAGS,
            Root.COLUMN_ICON,
            Root.COLUMN_TITLE,
            Root.COLUMN_DOCUMENT_ID,
    };

    // Используются  в качестве столбцов по умолчанию для возврата информации о
    // документе, если в запросе не запрашиваются конкретные столбцы
    private static final String[] DEFAULT_DOCUMENT_PROJECTION = new String[] {
            Document.COLUMN_DOCUMENT_ID,
            Document.COLUMN_MIME_TYPE,
            Document.COLUMN_DISPLAY_NAME,
            Document.COLUMN_FLAGS,
    };

    private static final String ROOT = "root";

    private FileRepository repo;
    private ConcurrentHashMap<String, FileEntity> fileCache = new ConcurrentHashMap<>();
    private boolean doCleanTmp = true;
    private ReentrantLock tmpLock = new ReentrantLock();

    @Override
    public boolean onCreate() {
        Log.i(TAG, "Create FileProvider");

        repo = FileRepository.getInstance(getContext().getApplicationContext());

        return true;
    }

    @Override
    public Cursor queryRoots(String[] projection) throws FileNotFoundException {
        Log.i(TAG, "queryRoots" + " ");

        // Создать курсор с запрошенными полями или проекцией по умолчанию. Этот
        // курсор возвращается в системный интерфейс выбора Android и используется для
        // отображения всех корневых каталого из этого провайдера
        MatrixCursor result = new MatrixCursor(resolveRootProjection(projection));

        // Создать одну строку для корневой директории
        MatrixCursor.RowBuilder row = result.newRow();
        row.add(Root.COLUMN_ROOT_ID, ROOT);
        // Имя корневого каталога, отображаемого системой
        row.add(Root.COLUMN_TITLE, getContext().getString(R.string.app_name));
        // Идентификатор документа должен быть уникальным в пределах провайдера и согласованным во времени.
        // Системный интерфейс может сохранить его и обратиться к нему позже
        row.add(Root.COLUMN_DOCUMENT_ID, getDocIdForFile(FileEntity.ROOT));
        row.add(Root.COLUMN_ICON, R.mipmap.ic_launcher);

        return result;
    }

    @Override
    public Cursor queryDocument(String documentId, String[] projection) throws FileNotFoundException {
        Log.i(TAG, "queryDocument=" + documentId);

        MatrixCursor result = new MatrixCursor(resolveDocumentProjection(projection));

        includeFile(result, documentId, null);

        return result;
    }

    @Override
    public Cursor queryChildDocuments(String parentDocumentId, String[] projection, String sortOrder)
            throws FileNotFoundException {
        Log.i(TAG, "queryChildDocuments=" + parentDocumentId + ", sortOrder=" + sortOrder);

        MatrixCursor result = new MatrixCursor(resolveDocumentProjection(projection));
        FileEntity parent = getFileForDocId(parentDocumentId);
        try {
            loadFileList(parent);

        } catch (IOException e) {
            throw new FileNotFoundException("Unable to get files list: " + Log.getStackTraceString(e));
        }
        for (FileEntity file : fileCache.values()) {
            includeFile(result, null, file);
        }

        return result;
    }

    @Override
    public ParcelFileDescriptor openDocument(String documentId, String mode, @Nullable CancellationSignal signal)
            throws FileNotFoundException {
        tmpLock.lock();
        Log.i(TAG, "openDocument=" + documentId + ", mode=" + mode);

        try {
            boolean isWrite = mode.indexOf('w') != -1;
            // Запись в файл запрещена
            if (isWrite) {
                return null;
            }

            FileEntity file = getFileForDocId(documentId);
            // Скачивать можно только файлы
            if (file.isDirectory()) {
                throw new FileNotFoundException("Failed to open file with id " + documentId +
                        " and mode " + mode + ": is a directory");
            }

            // Если временный файл уже существует, возвращаем его
            File tmp = getTmpFile(file);
            if (!tmp.exists()) {
                downloadFile(documentId, file, tmp);
            }

            int accessMode = ParcelFileDescriptor.parseMode(mode);
            ParcelFileDescriptor pfd;
            try {
                // Открываем временный файл для чтения приложением
                pfd = ParcelFileDescriptor.open(tmp, accessMode);

            } catch (IOException e) {
                throw new FileNotFoundException("Failed to open file with id " + documentId +
                        " and mode " + mode);
            }

            return pfd;

        } finally {
            tmpLock.unlock();
        }
    }

    private void downloadFile(String documentId, FileEntity file, File tmp) {
        // Запускаем отдельный поток для скачивания файла
        // Отдельный поток необходим чтобы предотвратить NetworkOnMainThreadException
        // в некоторый приложениях
        Thread t = new Thread(() -> {
            try {
                // Читаем из InputStream соединения с сервером и записываем во временный файл
                FileUtils.copyBytes(repo.openFile(file), new FileOutputStream(tmp));

            } catch (IOException | GeneralSecurityException e) {
                Log.e(TAG, "Failed to read file with id " +
                        documentId + ": " + Log.getStackTraceString(e));
            }
        });
        t.start();
        try {
            // Ждём завершения скачивания
            t.join();

        } catch (InterruptedException e) {
            Log.e(TAG, "Failed to wait for thread to finish");
        }
    }

    private static String[] resolveRootProjection(String[] projection) {
        return (projection != null ? projection : DEFAULT_ROOT_PROJECTION);
    }

    private static String[] resolveDocumentProjection(String[] projection) {
        return (projection != null ? projection : DEFAULT_DOCUMENT_PROJECTION);
    }

    private String getDocIdForFile(FileEntity file) {
        // Использовать путь как идентификатор
        return file.getPath();
    }

    private FileEntity getFileForDocId(String docId) throws FileNotFoundException {
        if (docId.equals(getDocIdForFile(FileEntity.ROOT))) {
            return FileEntity.ROOT;
        }

        FileEntity entity = fileCache.get(docId);
        if (entity == null) {
            // Либо файла нет, либо это директория, которая не находится в кеше
            entity = repo.makeDirEntity(docId);
            if (entity == null)
                throw new FileNotFoundException("Cannot find a file with id " + docId);
        }

        return entity;
    }

    /**
     * Загружаем список файлов и заполняем кеш
     */
    private void loadFileList(FileEntity dir) throws IOException {
        cleanTmpDir();
        fileCache.clear();

        List<FileEntity> entityList = repo.getFilesList(dir);
        for (FileEntity e : entityList) {
            fileCache.put(getDocIdForFile(e), e);
        }
    }

    private File getTmpFile(FileEntity file) {
        tmpLock.lock();

        try {
            doCleanTmp = true;
            // Заменяем слеши на нижнее подчёркивание
            String tmpName = getDocIdForFile(file).replace("/", "_");
            return new File(FileUtils.getTmpDir(getContext()), tmpName);

        } finally {
            tmpLock.unlock();
        }
    }

    private void cleanTmpDir() {
        tmpLock.lock();

        try {
            if (doCleanTmp) {
                try {
                    FileUtils.cleanTmpDir(getContext());
                    doCleanTmp = false;

                } catch (IOException e) {
                    Log.e(TAG, "Unable to clean tmp dir");
                }
            }

        } finally {
            tmpLock.unlock();
        }
    }

    /**
     * Добавляет файл в курсор
     */
    private void includeFile(MatrixCursor result, String docId, FileEntity file) throws FileNotFoundException {
        if (docId == null) {
            docId = getDocIdForFile(file);
        } else {
            file = getFileForDocId(docId);
        }

        final String displayName = file.getName();
        final String mimeType = getMimeType(file);

        final MatrixCursor.RowBuilder row = result.newRow();
        row.add(Document.COLUMN_DOCUMENT_ID, docId);
        row.add(Document.COLUMN_DISPLAY_NAME, displayName);
        row.add(Document.COLUMN_MIME_TYPE, mimeType);
    }

    private String getMimeType(FileEntity file) {
        if (file.isDirectory()) {
            return Document.MIME_TYPE_DIR;

        } else {
            return (file.getMimeType() == null ? "application/octet-stream" : file.getMimeType());
        }
    }
}
