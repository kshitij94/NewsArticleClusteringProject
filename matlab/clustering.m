fid = fopen('C:\\Users\\kshitij\\Desktop\\try\\keywords.txt','r');
keywords = {};


numOfArticles = 0;
while(~feof(fid))
    
    articleCount = fscanf(fid, '%d');
    line = fgets(fid);
%     
%     line = strrep(line, '\n','');
%     line = strrep(line, ';' , ' ');
%     line = strrep(line, '.' , ' ');
%     line = strrep(line, '''' , ' ');
%     line = lower(line);
%     
%     currentKeywords = strsplit(line,{',',' ','-'});
    
    
%     len = length(currentKeywords);
    
%     currentKeywords = strtrim(currentKeywords);
    
    currentKeywords = textToArray(line);
    keywords = [keywords, currentKeywords];
    numOfArticles = numOfArticles + 1  ;
end


for i = 1: length(keywords)
    keywords{i} = porterStemmer(keywords{i});
end



keywords = unique(keywords);
keywords = filterKeywords(keywords);
%  disp(keywords);

fid1 = fopen('C:\\Users\\kshitij\\Desktop\\try\\keywordindex.txt','w');

for i = 1: length(keywords)
    fprintf(fid1,'\n%s : %d', keywords{i},i);
    
end

fclose(fid);
fclose(fid1);

% mat = zeros(numOfArticles, length(keywords));
% 
% for i = 1 : (numOfArticles)
%     
%     filename = strcat(strcat('C:\\Users\\kshitij\\Desktop\\try\\',int2str(i-1)),'.txt');
%   
%     fid = fopen(filename,'r');
%     para = '';
%     while(~feof(fid))
%         para = strcat(para ,fgets(fid) );
%     end
%     
%     array = textToArray(para);
%     
%     for j = 1: length(array)
%         
%         findResult = getIndex(keywords, array{j});
%         
%         if((findResult) ~= -1)
%             
%             mat(i,findResult) = mat(i,findResult) + 1;
%             
%         end
%     end
%     fclose(fid);
%     fprintf('%d\n',i);
% end
%     
% 
%  outputToFile(mat,'C:\Users\kshitij\Desktop\try\out.xlsx',keywords);