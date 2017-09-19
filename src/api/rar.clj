(ns api.rar
  (:import [com.github.junrar Archive]
           [java.io ByteArrayOutputStream ByteArrayInputStream]))

(defn list-content! [file]
  (->> (.. (Archive. file) getFileHeaders)
       (filter #(-> % (. isDirectory) not))
       (map #(. % getFileNameString))
       sort))

(defn extract-content! [file page]
  (let [arc (Archive. file)
        header (nth (.. arc getFileHeaders) page)
        output-stream (ByteArrayOutputStream.)]
    (.. arc (extractFile header output-stream))
    (ByteArrayInputStream. (.. output-stream toByteArray))))
