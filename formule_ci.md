Source : https://fr.wikipedia.org/wiki/Cercle_circonscrit_%C3%A0_un_triangle#Premi%C3%A8re_%C3%A9criture,_par_un_d%C3%A9terminant

__Theoreme__

L'équation cartésienne du cercle circonscrit s'écrit : $\begin{vmatrix}
x^2+y^2 & x & y & 1 \\
x_A^2+y_A^2 & x_A & y_A & 1 \\
x_B^2+y_B^2 & x_B & y_B & 1 \\
x_C^2+y_C^2 & x_C & y_C & 1
\end{vmatrix}=0$


__Démo :__

L'équation d'un cercle dans un repère orthonormé est de la forme $x^2 + y^2 +\alpha x +\beta y +\gamma= 0$.
Les quatre points $M,A,B,C$ sont donc cocycliques si et seulement s'il existe trois constantes $\alpha,\beta,\gamma$ telles que :

$\begin{cases}
x^2+y^2+\alpha x+\beta y+\gamma&=0\\
x_A^2+y_A^2+\alpha x_A+\beta y_A+\gamma&=0\\
x_B^2+y_B^2+\alpha x_B+\beta y_B+\gamma&=0\\
x_C^2+y_C^2+\alpha x_C+\beta y_C+\gamma&=0\\
\end{cases}$.


L'existence d'un tel triplet de constantes équivaut à l'existence, dans le noyau de la matrice 
$N:=\begin{pmatrix}
x^2+y^2 & x & y & 1 \\
x_A^2+y_A^2 & x_A & y_A & 1 \\
x_B^2+y_B^2 & x_B & y_B & 1 \\
x_C^2+y_C^2 & x_C & y_C & 1
\end{pmatrix}$, d'un vecteur dont la première composante est $1$.

Comme les trois points $A,B,C$ sont supposés non alignés, le mineur $\begin{vmatrix}
x_A & y_A & 1 \\
x_B & y_B & 1 \\
x_C & y_C & 1
\end{vmatrix}$ est non nul, si bien que la condition précédente équivaut simplement à $\ker N\ne\{0\}$ donc à $\det N=0$
