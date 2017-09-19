(ns api.books
  (:require [api.rar :as rar]
            [clojure.java.io :as io]
            [image-resizer.resize :as img]
            [image-resizer.format :as fmt]
            [image-resizer.scale-methods :as m]
            [me.raynes.fs :as fs])
  (:import [java.util Base64]))

(defn encode [bs]
  (.encodeToString (Base64/getEncoder)
                   (if (string? bs) (. bs getBytes) bs)))

(defn decode [bs]
  (.decode (Base64/getDecoder)
           (if (string? bs) (. bs getBytes) bs)))

(defn- relative [base path]
  (drop (count (fs/split base))
        (drop-last (fs/split path))))

(defn find-files [base]
  (->> (fs/find-files base #".*\.cbr$")
       (map #(-> {:id (encode (str (fs/absolute %)))
                  :path (apply vector (relative base %))
                  :title (-> % fs/name fs/base-name)}))))

(defn list-content [id]
  (-> id decode String. io/file rar/list-content!))

(defn- cond-resize [img width]
  (if width
    (-> img
        ((img/resize-width-fn width m/speed))
        (fmt/as-stream-by-mime-type "image/jpeg"))
    img))

(defn get-content
  ([id entry] (get-content id entry {:width nil}))
  ([id entry {:keys [width]}]
   (-> id
       decode
       String.
       io/file
       (rar/extract-content! entry)
       (cond-resize width))))
