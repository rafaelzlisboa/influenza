In this challenge, suppose we are looking to do social network
analysis for prospective customers. We want to extract from
their social network a metric called "closeness centrality".

Centrality metrics try to approximate a measure of influence
of an individual within a social network. The distance between
any two vertices is their shortest path. The *farness*
of a given vertex *v* is the sum of all distances from each vertex
to *v*. Finally, the *closeness* of a vertex *v* is the inverse
of the *farness*.

The first part of the challenge is to rank the vertices in a given
graph by their *closeness*. The graph is provided in the attached
file; each line of the file consists of two vertex names separated by
a single space, representing an edge between those two nodes.

The second part of the challenge is to create a RESTful web server with
endpoints to register edges and to render a ranking of vertexes sorted by
centrality. We can think of the centrality value for a node as an initial "score"
for that customer.

The third and final part is to add another endpoint to flag a customer node as
"fraudulent". It should take a vertex id, and update the internal customer
score as such:


  - The fraudulent customer score should be zero.
  - Customers directly referred by the "fraudulent" customer
    should have their score halved.
  - Scores of customers indirectly referred by the "fraudulent" customer
    should be multiplied by a coefficient F:

    F(k) = (1 - (1/2)^k)

    where k is the shortest path from the "fraudulent" customer
    to the customer in question.
