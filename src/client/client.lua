json = require('json')
data = require('data')
clientID = "turtle:1"
lastFuelLevel = -1

--Main functions
function setup()
    print("Running Setup")
    ws = assert(http.websocket("ws://localhost:8080"))
    local computerLabel = os.getComputerLabel()
    if (computerLabel ~= nil) then 
        clientID = "turtle:" .. computerLabel 
    else
        clientID = "turtle:" .. os.getComputerID()
    end
    xyzd = data.getInfo()
end

function loop()
    local msg = json.decode(ws.receive())
    if (msg['type'] == 'REQINIT') then
        lastFuelLevel = turtle.getFuelLevel()
        ws.send(json.encode({type= "INIT", from=clientID, to="server", args={}}))
        ws.send(json.encode({type= "INFO", from=clientID, to="all", args={request=req,res=json.encode({fuelLevel=lastFuelLevel,fuelLimit=turtle.getFuelLimit(),x=xyzd[1],y=xyzd[2],z=xyzd[3],d=xyzd[4]})}}))
    elseif (msg['type'] == 'REQINFO') then
        local req = msg['args']['request']
        print("REQINFO " .. req)
        if (req == 'fuelLevel') then
            lastFuelLevel = turtle.getFuelLevel()
            ws.send(json.encode({type= "INFO", from=clientID, to=msg['from'], args={request=req,res=lastFuelLevel}}))
        elseif (req == 'fuelLimit') then
            ws.send(json.encode({type= "INFO", from=clientID, to=msg['from'], args={request=req,res=turtle.getFuelLimit()}}))
        elseif (req == 'turtleInfo') then
            lastFuelLevel = turtle.getFuelLevel()
            ws.send(json.encode({type= "INFO", from=clientID, to=msg['from'], args={request=req,res=json.encode({fuelLevel=lastFuelLevel,fuelLimit=turtle.getFuelLimit(),x=xyzd[1],y=xyzd[2],z=xyzd[3],d=xyzd[4]})}}))
        end
    elseif (msg['type'] == 'COMMAND') then
        local action = msg['args']['action']
        print("COMMAND " .. action)
        if (action == 'Forward') then
            if (turtle.forward()) then
                xyzd = data.forward()
                ws.send(json.encode({type= "INFO", from=clientID, to="all", args={request='turtleInfo',res=json.encode({x=xyzd[1],y=xyzd[2],z=xyzd[3],d=xyzd[4]})}}))
            end
        elseif (action == 'Backward') then
            if (turtle.back()) then
                xyzd = data.back()
                ws.send(json.encode({type= "INFO", from=clientID, to="all", args={request='turtleInfo',res=json.encode({x=xyzd[1],y=xyzd[2],z=xyzd[3],d=xyzd[4]})}}))
            end
        elseif (action == 'Up') then
            if (turtle.up()) then
                xyzd = data.up()
                ws.send(json.encode({type= "INFO", from=clientID, to="all", args={request='turtleInfo',res=json.encode({x=xyzd[1],y=xyzd[2],z=xyzd[3],d=xyzd[4]})}}))
            end
        elseif (action == 'Down') then
            if (turtle.down()) then
                xyzd = data.down()
                ws.send(json.encode({type= "INFO", from=clientID, to="all", args={request='turtleInfo',res=json.encode({x=xyzd[1],y=xyzd[2],z=xyzd[3],d=xyzd[4]})}}))
            end
        elseif (action == 'TurnLeft') then
            if (turtle.turnLeft()) then
                xyzd = data.turnLeft()
                ws.send(json.encode({type= "INFO", from=clientID, to="all", args={request='turtleInfo',res=json.encode({x=xyzd[1],y=xyzd[2],z=xyzd[3],d=xyzd[4]})}}))
            end
        elseif (action == 'TurnRight') then
            if (turtle.turnRight()) then
                xyzd = data.turnRight()
                ws.send(json.encode({type= "INFO", from=clientID, to="all", args={request='turtleInfo',res=json.encode({x=xyzd[1],y=xyzd[2],z=xyzd[3],d=xyzd[4]})}}))
            end
        elseif (action == 'Dig') then
            turtle.dig()
        elseif (action == 'DigUp') then
            turtle.digUp()
        elseif (action == 'DigDown') then
            turtle.digDown()
        elseif (action == 'Place') then
            turtle.place()
        elseif (action == 'PlaceUp') then
            turtle.placeUp()
        elseif (action == 'PlaceDown') then
            turtle.placeDown()
        elseif (action == 'Attack') then
            turtle.attack()
        elseif (action == 'AttackUp') then
            turtle.attackUp()
        elseif (action == 'AttackDown') then
            turtle.attackDown()
        elseif (action == 'Suck') then
            turtle.suck()
        elseif (action == 'SuckUp') then
            turtle.suckUp()
        elseif (action == 'SuckDown') then
            turtle.suckDown()
        elseif (action == 'Inspect') then
            has_block, blockdata = turtle.inspect()
            ws.send(json.encode({type= "INFO", from=clientID, to=msg['from'], args={request="inspect",res=json.encode(blockdata)}}))
        elseif (action == 'InspectUp') then
            has_block, blockdata = turtle.inspectUp()
            ws.send(json.encode({type= "INFO", from=clientID, to=msg['from'], args={request="inspectUp",res=json.encode(blockdata)}}))
        elseif (action == 'InspectDown') then
            has_block, blockdata = turtle.inspectDown()
            ws.send(json.encode({type= "INFO", from=clientID, to=msg['from'], args={request="inspectDown",res=json.encode(blockdata)}}))
        elseif (action == 'Lua') then
            returnmsg = {type= "INFO", from=clientID, to=msg['from'], args={request="lua",res="Error in Load"}}
            local cmd = msg['args']['lua']
            cmd = ("local res = 'no output'; %s; if (res == nil) then res = 'nil' end; return res;"):format(cmd)
            local cmdfunc = load(cmd)
            if (cmdfunc ~= nil) then 
                local status, res = pcall(cmdfunc)
                if (type(res) == "table") then res = json.encode(res) end
                returnmsg['args']['res'] = res
            end
            ws.send(json.encode(returnmsg))
        elseif (action == 'Refuel') then
            refuel()
        elseif (action == 'Terminate') then
            exit()
        end
    end
    
    local currentFuelLevel = turtle.getFuelLevel()
    if (currentFuelLevel ~= lastFuelLevel) then
        ws.send(json.encode({type= "INFO", from=clientID, to="all", args={request="fuelLevel",res=currentFuelLevel}}))
        lastFuelLevel = currentFuelLevel
    end
end

function terminate()
    print("Terminating")
    ws.close()
end

--Escape
escape = false
function exit()
    escape = true
end

--Lib
function has_value(tab, val)
    for i, v in ipairs(tab) do
        if v == val then return true end
    end
    return false
end



function refuel()
    local level = turtle.getFuelLevel()
    local limit = turtle.getFuelLimit()

    if (level / limit > 1) then --adjust this to change refueling threshold
        return level
    end

    if level == "unlimited" then 
        print("Fuel level is unlimited")
        return -1
    end

    fuelItems = {"minecraft:coal", "minecraft:charcoal", "minecraft:coal_block"}
    local prevSlot = turtle.getSelectedSlot()
    local hasFuel = false
    for n = 1,16 do
        item = turtle.getItemDetail(n)
        if item ~= nil and has_value(fuelItems, item["name"]) then 
            turtle.select(n)
            hasFuel = true
            break
        end
    end
    if not hasFuel then
        print("[REFUEL] No available fuel item")
        return level
    end

    local res, err = turtle.refuel()
    if res then
        local new_level = turtle.getFuelLevel()
        print(("[REFUEL] Refuelled %d, current level is %d"):format(new_level - level, new_level))
    else
        print(("[REFUEL] refueling error: "):format(err))
        local new_level = turtle.getFuelLevel()
    end

    turtle.select(prevSlot)
    return new_level
end

setup()
while not escape do loop() end
terminate()