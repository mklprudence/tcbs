--Retrieve furnace items
local furnace = peripheral.find('minecraft:furnace');res=furnace.list();res['encode_as_object']=true

--Retrieve chest items
res=peripheral.find("minecraft:chest").list();res["encode_as_object"]=true

--Move items
peripheral.wrap('bottom').pushItems('bottom',3,64,1)