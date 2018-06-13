options (skip = 1,rows = 128)  
LOAD DATA   
INFILE 'C:\Users\lenovo\Documents\gitcode\thucp\data\CPMRM\test.csv'   
  
append  
INTO TABLE ORDER_TEST  
fields terminated by ','  
Optionally enclosed by '"'  
  (  
   GRBM,  
  F_MC,  
  DL,  
  F_ZL,
  F_DJ,
  F_ZJE,
  F_RQ
   )  