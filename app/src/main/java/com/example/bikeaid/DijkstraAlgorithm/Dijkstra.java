package com.example.bikeaid.DijkstraAlgorithm;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Dijkstra {
    private Map<String, Vertex> vertexNames;


    public Dijkstra() {
        vertexNames = new HashMap<>();
    }


    public void addVertex(Vertex v) {
//        if (vertexNames.containsKey(v.name))
//            throw new IllegalArgumentException("Cannot create new vertex with existing name.");
        vertexNames.put(v.name, v);
    }


    public Collection<Vertex> getVertices() {
        return vertexNames.values();
    }

    public Vertex getVertex(String name) {
        return vertexNames.get(name);
    }


    public void addEdge(String nameU, String nameV, Double cost) {
        Log.d("TEST VALUE", "" + nameU);
        Log.d("TEST VALUE", "" + nameV);
        if (!vertexNames.containsKey(nameU))
            throw new IllegalArgumentException(nameU + " does not exist. Cannot create edge.");
        if (!vertexNames.containsKey(nameV))
            throw new IllegalArgumentException(nameV + " does not exist. Cannot create edge.");
        Vertex sourceVertex = vertexNames.get(nameU);
        Vertex targetVertex = vertexNames.get(nameV);
        Edge newEdge = new Edge(sourceVertex, targetVertex, cost);
        sourceVertex.addEdge(newEdge);
    }


    public void addUndirectedEdge(String nameU, String nameV, double cost) {
        addEdge(nameU, nameV, cost);
        addEdge(nameV, nameU, cost);
    }


    public double computeEuclideanDistance(double ux, double uy, double vx, double vy) {
        double diffx = vx - ux;
        double diffy = vy - uy;
        double sumSquared = Math.pow(diffx, 2) + Math.pow(diffy, 2);
        // return 1.0; // Replace this
        return Math.pow(sumSquared, 0.5);
    }


    public void computeAllEuclideanDistances() {
        int dijkstraSize = vertexNames.size();
        //iterating through the hashMap
        for (String u : vertexNames.keySet())
        //Iterator it=vertexNames.keySet().iterator();
        //while(it.hasNext())
        {
//	        String nameOfVertex = it.next();
            Vertex node = vertexNames.get(u);
            double sourceX = node.x;
            double sourceY = node.y;
            List adjacencyList = node.adjacentEdges;
            for (Edge e : vertexNames.get(u).adjacentEdges) {
                // for (int i=0; i< adjacencyList.size(); i++)
                // {


//	          Edge edge= adjacencyList.get(i);
                double targetX = e.target.x;
                double targetY = e.target.y;
                double updatedDistance = computeEuclideanDistance(sourceX, sourceY, targetX, targetY);
                e.distance = updatedDistance;

            }

        }


    }

    public void doDijkstra(String s) {
        //setting sentinel value
        double MAX_VALUE = 1000000;

        for (String u : vertexNames.keySet()) {
            Vertex node = vertexNames.get(u);
            node.distance = MAX_VALUE;
            node.prev = null;
            node.known = false;

        }
        vertexNames.get(s).distance = 0;

        LinkedList<Vertex> vertexList = new LinkedList<>();
        for (String u : vertexNames.keySet()) {
            vertexList.addLast(vertexNames.get(u));
        }

        while (vertexList.size() > 0) {

            int index = findClosestVertex(vertexList);
            // Vertex v = vertexList.closestVertex();
            Vertex v = vertexList.get(index);
            v.known = true;
            ///once we have discovered a vertex, we remove it from the list
            vertexList.remove(index);

            List<Edge> adjacencyList = v.adjacentEdges;

            for (Edge e : adjacencyList) {
                if (!e.target.known) {
                    double distanceCost = e.distance;
                    if (v.distance + distanceCost < e.target.distance) {
                        e.target.distance = v.distance + distanceCost;
                        e.target.prev = v;
                    }


                }
            }


        }
        // vertexNames.get(s).distance=0;
        // //creating list of vertices which are known
        // LinkedList<Vertex> discovered = new LinkedList<>();
        // discovered.addFirst(s);

        //   for (Edge e : vertexNames.get(s).adjacentEdges)
        //   {
        //     compareDistance=s.distance + e.distance;
        //     if (e.target.distance > compareDistance)
        //     {
        //       e.target.distance=compareDistance;
        //     }
        //   }

        //   nextVertex=s.findClosestVertex();
        ///method to find the closest vertex in the adjacency list of the discovered vertex

    }

    public static int findClosestVertex(LinkedList<Vertex> list) {
        // double minDist=v.adjacentEdges[0].target.distance;
        // Vertex closestVertex=v.adjacentEdges[0].target;
        double minDist = list.get(0).distance;
        int minIndex = 0;
        int i = 0;

        for (i = 1; i < list.size(); i++) {
            double dist = list.get(i).distance;
            if (dist < minDist) {
                minDist = dist;
                minIndex = i;
                // closestVertex=list[i];
            }

        }
        return minIndex;
    }


    public List<Edge> getDijkstraPath(String s, String t) {
        doDijkstra(s);

        Vertex v = vertexNames.get(t);
        LinkedList<Edge> shortestPathEdges = new LinkedList<>();
        Vertex penultimateVertex = null;

        while (v.prev != null) {
            List<Edge> adjacencyList = v.adjacentEdges;
            for (Edge e : adjacencyList) {
                if (e.target.equals(v.prev)) {
                    shortestPathEdges.addFirst(e);
                }
            }

            penultimateVertex = v;
            v = v.prev;
        }
        for (Edge e : v.adjacentEdges) {
            if (e.target.equals(penultimateVertex)) {
                shortestPathEdges.addFirst(e);
            }
        }
        return shortestPathEdges;
    }


    public void printAdjacencyList() {
        for (String u : vertexNames.keySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(u);
            sb.append(" -> [ ");
            for (Edge e : vertexNames.get(u).adjacentEdges) {
                sb.append(e.target.name);
                sb.append("(");
                sb.append(e.distance);
                sb.append(") ");
            }
            sb.append("]");
            System.out.println(sb.toString());
        }
    }

//    public static void main(String[] argv) throws IOException {
//        String vertexFile = "cityxy.txt";
//        String edgeFile = "citypairs.txt";
//
//        Dijkstra dijkstra = new Dijkstra();
//        String line;
//
//        // Read in the vertices
//        BufferedReader vertexFileBr = new BufferedReader(new FileReader(vertexFile));
//        while ((line = vertexFileBr.readLine()) != null) {
//            String[] parts = line.split(",");
//            if (parts.length != 3) {
//                vertexFileBr.close();
//                throw new IOException("Invalid line in vertex file " + line);
//            }
//            String cityname = parts[0];
//            int x = Integer.valueOf(parts[1]);
//            int y = Integer.valueOf(parts[2]);
//            Vertex vertex = new Vertex(cityname, x, y);
//            dijkstra.addVertex(vertex);
//        }
//        vertexFileBr.close();
//
//        BufferedReader edgeFileBr = new BufferedReader(new FileReader(edgeFile));
//        while ((line = edgeFileBr.readLine()) != null) {
//            String[] parts = line.split(",");
//            if (parts.length != 3) {
//                edgeFileBr.close();
//                throw new IOException("Invalid line in edge file " + line);
//            }
//            dijkstra.addUndirectedEdge(parts[0], parts[1], Double.parseDouble(parts[2]));
//        }
//        edgeFileBr.close();
//
//        // Compute distances.
//        // This is what happens when you click on the "Compute All Euclidean Distances" button.
//        dijkstra.computeAllEuclideanDistances();
//
//        // print out an adjacency list representation of the graph
//        dijkstra.printAdjacencyList();
//
//        // This is what happens when you click on the "Draw Dijkstra's Path" button.
//
//        // In the GUI, these are set through the drop-down menus.
//        String startCity = "SanFrancisco";
//        String endCity = "Boston";
//
//        // Get weighted shortest path between start and end city.
//        List<Edge> path = dijkstra.getDijkstraPath(startCity, endCity);
//
//        System.out.print("Shortest path between "+startCity+" and "+endCity+": ");
//        System.out.println(path);
//    }

}