/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.GriffinLamps.pagina.Service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {

    @Value("${firebase.bucket.name}")
    private String bucketName;
    @Value("${firebase.storage.path}")
    private String storagePath;
    // Aquí se manejaría la inyección del cliente de Storage como un bean
    private final Storage storage;

    public FirebaseStorageService(Storage storage) {
        this.storage = storage;
    }

    public String uploadImage(MultipartFile localFile, Integer id) throws IOException {
        String originalName = localFile.getOriginalFilename();
        String fileExtension = "";
        String baseName = "imagen";

        if (originalName != null && originalName.contains(".")) {
            fileExtension = originalName.substring(originalName.lastIndexOf("."));
            baseName = originalName.substring(0, originalName.lastIndexOf("."));
        }

        String fileName = baseName + fileExtension;

        File tempFile = convertToFile(localFile);
        try {
            return uploadToFirebase(tempFile, id, fileName);
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public String uploadColeccionImage(MultipartFile localFile, Integer id) throws IOException {
        String originalName = localFile.getOriginalFilename();
        String fileExtension = "";
        String baseName = "imagen";

        if (originalName != null && originalName.contains(".")) {
            fileExtension = originalName.substring(originalName.lastIndexOf("."));
            baseName = originalName.substring(0, originalName.lastIndexOf("."));
        }

        String fileName = baseName + fileExtension;
        File tempFile = convertToFile(localFile);
        try {
            BlobId blobId = BlobId.of(bucketName, storagePath + "/colecciones/" + id + "/" + fileName);
            String mimeType = Files.probeContentType(tempFile.toPath());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(mimeType != null ? mimeType : "media")
                    .build();
            storage.create(blobInfo, Files.readAllBytes(tempFile.toPath()));
            return storage.signUrl(blobInfo, 1825, TimeUnit.DAYS).toString();
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    public void deleteImage(String signedUrl) {
        try {
            String path = signedUrl.split("\\?")[0];
            String blobName = path.substring(path.indexOf(storagePath));

            BlobId blobId = BlobId.of(bucketName, blobName);
            boolean deleted = storage.delete(blobId);

            if (!deleted) {
                System.err.println("No se pudo eliminar la imagen: " + blobName);
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar imagen de Firebase: " + e.getMessage());
        }
    }

    //Convierte un MultipartFile a un archivo temporal en el servidor.
    private File convertToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("upload-", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    //Sube el archivo al almacenamiento de Firebase y genera una URL firmada.     
    private String uploadToFirebase(File file, Integer id, String fileName) throws IOException {
        // ✅ Ruta: productos/1/imgXXXXXXXXXXXXXX.jpg
        BlobId blobId = BlobId.of(bucketName, storagePath + "/" + id + "/" + fileName);
        String mimeType = Files.probeContentType(file.toPath());
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(mimeType != null ? mimeType : "media")
                .build();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
        return storage.signUrl(blobInfo, 1825, TimeUnit.DAYS).toString();
    }

    /**
     * Genera un string numérico con un formato de 14 dígitos, rellenado con
     * ceros a la izquierda.
     */
    private String getFormattedNumber(long id) {
        return String.format("%014d", id);
    }
}
