library(SparkR)

# Logistic regression in Spark.
# Note: unlike the example in Scala, a point here is represented as a vector of
# doubles.

parseVectors <-  function(lines) {
  lines <- strsplit(as.character(lines) , " ", fixed = TRUE)
  list(matrix(as.numeric(unlist(lines)), ncol = length(lines[[1]])))
}

dist.fun <- function(P, C) {
  apply(
    C,
    1, 
    function(x) { 
      colSums((t(P) - x)^2)
    }
  )
}

closestPoint <-  function(P, C) {
  max.col(-dist.fun(P, C))
}
# Main program

args <- commandArgs(trailing = TRUE) 

if (length(args) != 4) {
  print("Usage: kmeans <master> <file> <K> <convergeDist>")
  q("no")
}

sc <- sparkR.init(args[[1]], "RKMeans")
K <- as.integer(args[[3]])
convergeDist <- as.double(args[[4]])

lines <- SparkR:::textFile(sc, args[[2]])

points <- cache(SparkR:::lapplyPartition(lines, parseVectors))
# kPoints <- take(points, K)
kPoints <- do.call(rbind, SparkR:::takeSample(points, FALSE, K, 16189L))
tempDist <- 1.0
iter <- 0


while (iter < convergeDist) {
  

  closest <- SparkR:::lapplyPartition(
    SparkR:::lapply(points,
           function(p) {
             cp <- closestPoint(p, kPoints); 
             mapply(list, unique(cp), split.data.frame(cbind(1, p), cp), SIMPLIFY=FALSE)
           }),
    function(x) {do.call(c, x)
    })
  
  pointStats <- SparkR:::reduceByKey(closest,
                            function(p1, p2) {
                              t(colSums(rbind(p1, p2)))
                            },
                            2L)
  
  newPoints <- do.call(
    rbind,
    collect(SparkR:::lapply(pointStats,
                   function(tup) {
                     point.sum <- tup[[2]][, -1]
                     point.count <- tup[[2]][, 1]
                     point.sum/point.count
                   })))
  
  D <- dist.fun(kPoints, newPoints)
  tempDist <- sum(D[cbind(1:3, max.col(-D))])
  kPoints <- newPoints
  cat("Finished iteration (delta = ", tempDist, ")\n")
  
  iter = iter +1
  print(iter) 
}

cat("Final centers:\n")
writeLines(unlist(lapply(kPoints, paste, collapse = " ")))
print(D)

