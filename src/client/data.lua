local DIRECTIONS = {"north", "east", "south", "west"}

-- UTILS
local function split(s)
    split= {}
    for word in s:gmatch("%w+") do table.insert(split, word) end
    return split
end

local function getKeyFromValue(t, val)
    for k,v in pairs(t) do
        if (v == val) then return k end
    end
    return nil
end

-- FS
local function readFile()
    local file = fs.open("data.txt", "r")
    local elements = {}
    if (file ~= nil) then
        local contents = file.readAll()
        file.close()
        elements = split(contents)
    end
    count = #elements

    if (count > 0) then X = tonumber(elements[1]) else X = 0 end
    if (count > 1) then Y = tonumber(elements[2]) else Y = 0 end
    if (count > 2) then Z = tonumber(elements[3]) else Z = 0 end
    if (count > 3) then D = tonumber(elements[4]) else D = 0 end
end

local function writeFile()
    local file = fs.open("data.txt", "w")
    if (X == nil) then X = 0 end
    if (Y == nil) then Y = 0 end
    if (Z == nil) then Z = 0 end
    if (D == nil) then D = 0 end
    file.write(string.format("%s %s %s %s", X, Y, Z, D))
    file.close()
end

-- GETTERS
local function getLocation()
    if (X == nil or Y == nil or Z == nil) then readFile() end
    return {X, Y, Z}
end

local function getDirection()
    if (D == nil) then readFile() end
    return D
end

local function getInfo()
    if (X == nil or Y == nil or Z == nil or D == nil) then readFile() end
    return {X, Y, Z, D}
end

-- SETTERS
local function setLocation(loc)
    if (#loc < 3) then error("Invalid input loc") end
    X = loc[1]
    Y = loc[2]
    Z = loc[3]
    writeFile()
end

local function setDirection(dir)
    if (type(dir) == "string") then
        D = getKeyFromValue(DIRECTIONS, dir) - 1
    elseif (type(dir) == "number") then
        D = dir
    else
        error("Invalid input dir")
    end
    writeFile()
end

local function forward()
    if (X == nil or Y == nil or Z == nil or D == nil) then readFile() end
    if (D == 0) then
        Z = Z - 1
    elseif (D == 1) then
        X = X + 1
    elseif (D == 2) then
        Z = Z + 1
    elseif (D == 3) then
        X = X - 1
    else 
        error("WTF")
    end
    writeFile()
    return getInfo()
end

local function back()
    if (X == nil or Y == nil or Z == nil or D == nil) then readFile() end
    if (D == 0) then
        Z = Z + 1
    elseif (D == 1) then
        X = X - 1
    elseif (D == 2) then
        Z = Z - 1
    elseif (D == 3) then
        X = X + 1
    else 
        error("WTF")
    end
    writeFile()
    return getInfo()
end

local function up()
    if (X == nil or Y == nil or Z == nil or D == nil) then readFile() end
    Y = Y + 1
    writeFile()
    return getInfo()
end

local function down()
    if (X == nil or Y == nil or Z == nil or D == nil) then readFile() end
    Y = Y - 1
    writeFile()
    return getInfo()
end

local function turnLeft()
    if (D == nil) then readFile() end
    D = (D - 1) % 4
    writeFile()
    return getInfo()
end

local function turnRight()
    if (D == nil) then readFile() end
    D = (D + 1) % 4
    writeFile()
    return getInfo()
end

return {
    _version = "0.0.1",
    DIRECTIONS = DIRECTIONS,
    forward = forward,
    back = back,
    up = up,
    down = down,
    turnLeft = turnLeft,
    turnRight = turnRight,
    getInfo = getInfo,
}

