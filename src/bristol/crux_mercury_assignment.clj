; https://juxt.pro/blog/crux-tutorial-datalog

(ns bristol.crux-mercury-assignment
  (:require [crux.api :as crux]
            [clojure.edn :as edn]))

; 1. make a node
(def node (crux/start-node {}))

; 2. make db out of node
(def db (crux/db node))

(defn easy-ingest
  "Uses Crux put transaction to add a vector of
  documents to a specified system"
  [node docs]
  (crux/submit-tx node
                  (vec (for [doc docs] [:crux.tx/put doc]))))

(defonce data (edn/read-string (slurp "https://raw.githubusercontent.com/johantonelli/tutorials/master/tutorials.crux/resources/mercury.txt")))

(easy-ingest node data)

;; (def db (crux/db node))

(crux/q (crux/db node)
        '{:find [element]
          :where [[element :type :element/metal]]})

(crux/q db
        '{:find [element]
          :where [[element :type :element/metal]]})

; Example 2
(=
 (crux/q (crux/db node)
         '{:find [element]
           :where [[element :type :element/metal]]})

 (crux/q (crux/db node)
         {:find '[element]
          :where '[[element :type :element/metal]]})

 (crux/q (crux/db node)
         (quote
          {:find [element]
           :where [[element :type :element/metal]]})))

; Example 3
(crux/q (crux/db node)
        '{:find [name]
          :where [[e :type :element/metal]
        [e :common-name name]]})


; Example 4
(crux/q (crux/db node)
        '{:find [name rho]
          :where [[e :density rho]
                   [e :common-name name]]})

; Example 5
(crux/q (crux/db node)
        {:find '[name]
         :where '[[e :type t]
                  [e :common-name name]]
                 :args [{'t :element/metal}]})

; detour using :in instead of :args
(crux/q (crux/db node)
        {:find '[name]
         :in ['t]
         :where '[[e :type t]
                  [e :common-name name]]} 
        :element/metal)

'{:crux.db/id :commodity/Pu
    :common-name "Plutonium"
    :type :element/metal
    :density 19.816
    :radioactive true}

; relation binding

(crux/q (crux/db node)
        '{:find [element-type common-name e]
          :in [[[element-type common-name]]]
         :where [[e :common-name common-name]]}
 [[:element/metal "Plutonium"]
  [:element/gas "Nitrogen"]])
;; => #{[:element/metal "Plutonium" :commodity/Pu] [:element/gas "Nitrogen" :commodity/N]}

(crux/q (crux/db node)
        '{:find [element-type common-name]
          :in [[[element-type common-name]]] }
 [[:element/metal "Plutonium"]
  [:element/gas "Nitrogen"]])
;; => #{[:element/metal "Plutonium"] [:element/gas "Nitrogen"]}

; Last example
(defn filter-type
  [type]
  (crux/q (crux/db node)
          {:find '[name]
           :where '[[e :type t]
                    [e :common-name name]]
           :args [{'t type}]}))

(defn filter-appearance
  [description]
  (crux/q (crux/db node)
          {:find '[name IUPAC]
           :where '[[e :common-name name]
                    [e :IUPAC-name IUPAC]
                    [e :appearance appearance]]
           :args [{'appearance description}]}))

(filter-type :element/metal)
;; => #{["Gold"] ["Plutonium"]}


(filter-appearance "white solid")
;; => #{["Borax" "Sodium tetraborate decahydrate"]}

; (def manifest
;   {:crux.db/id :manifest
;    :pilot-name "Johanna"
;    :id/rocket "SB002-sol"
;    :id/employee "22910x2"
;    :badges "SETUP"
;    :cargo ["stereo" "gold fish" "slippers" "secret note"]})
(require '[bristol.crux-earth-assignment :as earth])

(crux/submit-tx
 node [[:crux.tx/put (assoc earth/manifest
                            :badges ["SETUP" "PUT" "DATALOG-QUERIES"])]])

; next time Neptune assignment