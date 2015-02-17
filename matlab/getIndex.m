function index = getIndex(cells, findstr)
    index = -1;
    for i = 1:length(cells)
        if(strcmp(cells{i}, findstr))
            index = i;
            break;
        end
    end
end