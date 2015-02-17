function array = textToArray(text)
    
    text = lower(text);
    
    array = strsplit(text,{',',' ','-','\n',';','.','''','!','?',':','/','\'});
    array = strtrim(array);
    
    
    for i = 1: length(array)
        array{i} = porterStemmer(array{i});
    end

    array = filterKeywords(array);
end