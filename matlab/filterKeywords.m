function retVal = filterKeywords(keys)
    

        
    filters = {'what','you','your','is','the','a','am','was','who','why','will','when','there','thank','that','take','tab1','over','how','for','did','ask','all'};
    retVal = {};
    firsttime  = 1;
    for i = 1: length(keys)
        flag = 0;
        for j = 1: length(filters)
            if strcmp(keys{i},filters{j}) 
                %disp(keys{i});
                flag = 1;
                break;
            end

        end

        if flag == 0
            if firsttime == 1
                retVal = {keys{i}};
                
                
                firsttime = 0;
            else
                retVal = [retVal , keys{i}];
            end
        end
    end
    
    temp = {};
    for i = 1:length(retVal)
        if(length(retVal{i}) > 2 && isempty(regexp(retVal{i},'\d','ONCE')))
            temp = [temp, retVal{i}];
        end
    end
    retVal = temp;
end