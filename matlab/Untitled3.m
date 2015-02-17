mat = xlsread('C:\Users\kshitij\Desktop\try\out.xlsx');
size(mat)
[row, col] = size(mat);
for j = 1:col
    numOfZeros = length(find(mat(:,j) == 0));
    filledPercentage = (200 - numOfZeros)/2;
    if(filledPercentage > 5)
        fprintf('\ncolumn %d : filled percentage : %d', j,filledPercentage);
    end
    
end