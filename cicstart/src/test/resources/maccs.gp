#gnuplot -e "filename='PGG20120108_l0_sec.sec'" maccs.gp
set terminal postscript eps size 16,9 enhanced color font 'Helvetica,20' linewidth 2
set output filename.".eps"
set timefmt "%Y| %m| %d| %H| %M| %S|"
set xdata time
plot filename every ::13 using 1:7 with lines linestyle 1
#convert -density 300 -alpha off PGG20120108_l0_sec.eps PGG20120108_l0_sec.png
