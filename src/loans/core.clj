(ns loans.core
  (:require [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [java-time :as jt]))

(defn ->loan [[provider amount date]]
  {:provider provider
   :amount (Integer/parseInt amount)
   :date (jt/local-date date)})

(defn sort-by-date [items]
  (sort-by :date jt/before? items))

(defn get-loans []
  (with-open [reader (io/reader "resources/loans.csv")]
    (->> (csv/read-csv reader)
         (rest)
         (map ->loan)
         (sort-by-date)
         doall)))

(defn filter-by-month [month items]
  (filter #(= (.getMonthValue (:date %)) month) items))

(defn get-segment [half items]
  (let [[l u] (case half
                      :first [0 15]
                      :second [15 31]
                      [0 31])]
    (filter #(and 
              (> (.getDayOfMonth (:date %)) l)
              (<= (.getDayOfMonth (:date %)) u)) items)))


(comment
  (->> (get-loans)
       (filter-by-month 8)
       (get-segment :full))

  (defn d-comp [d1 d2]
    (.before? d1 d2))

  (->> (get-loans)
       (sort-by-date))
  )