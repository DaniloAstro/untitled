package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class MediaDownloader {
    private List<String> urls;
    private Semaphore semaphore;
    private List<Integer> downloadedSizes;
    private CyclicBarrier barrier;
    private AtomicInteger maxFileSize;
    private AtomicInteger minFileSize;

    private static final String DOWNLOAD_FOLDER = "downloaded";

    public MediaDownloader(List<String> urls, int maxConcurrentDownloads) {
        this.urls = urls;
        this.semaphore = new Semaphore(maxConcurrentDownloads);
        this.downloadedSizes = new CopyOnWriteArrayList<>();
        this.barrier = new CyclicBarrier(urls.size() + 1);
        this.maxFileSize = new AtomicInteger(Integer.MIN_VALUE);
        this.minFileSize = new AtomicInteger(Integer.MAX_VALUE);

        createDownloadFolder();
    }

    private void createDownloadFolder() {
        Path path = Paths.get(DOWNLOAD_FOLDER);
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadMedia(String url) {
        try {
            semaphore.acquire();


            String fileName = url.substring(url.lastIndexOf('/') + 1);


            Path filePath = Paths.get(DOWNLOAD_FOLDER, fileName);


            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));


            int fileSize = ThreadLocalRandom.current().nextInt(1024, 102400);


            maxFileSize.updateAndGet(current -> Math.max(current, fileSize));
            minFileSize.updateAndGet(current -> Math.min(current, fileSize));


            downloadedSizes.add(fileSize);


            Files.write(filePath, new byte[fileSize]);


            System.out.println("Downloaded " + url + ", Size: " + fileSize + " bytes, Saved to: " + filePath.toAbsolutePath());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
    }

    private Runnable createTask(String url) {
        return () -> {
            try {
                downloadMedia(url);
            } finally {
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(urls.size());

        for (String url : urls) {
            executorService.submit(createTask(url));
        }

        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        calculateAverageSize();
    }

    private void calculateAverageSize() {
        int totalSize = downloadedSizes.stream().mapToInt(Integer::intValue).sum();
        double averageSize = (double) totalSize / downloadedSizes.size();
        System.out.println("Average size of downloaded files: " + averageSize + " bytes");
        System.out.println("Max file size: " + maxFileSize.get() + " bytes");
        System.out.println("Min file size: " + minFileSize.get() + " bytes");
    }
}

public class Main {
    public static void main(String[] args) {
        List<String> urls = List.of("https://www.greeneconomycoalition.org/assets/images/theme-measurement/_figure/alexandr-schwarz-660.jpg",
                "https://www.gaunt.dev/generated/berries-4.4917a8d/800.jpg",
                "https://www.gaunt.dev/generated/pi-crates.1a36f78/800.jpg");
        int maxConcurrentDownloads = 2;

        MediaDownloader downloader = new MediaDownloader(urls, maxConcurrentDownloads);
        downloader.run();
    }
}
