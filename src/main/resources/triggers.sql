ALTER TABLE  `usuarios` ADD  `bans` INT( 3 ) NOT NULL DEFAULT  '0';
ALTER TABLE  `usuarios` ADD  `kicks` INT( 3 ) NOT NULL DEFAULT  '0';

CREATE TRIGGER add_ban
BEFORE INSERT ON Ban FOR EACH ROW
BEGIN
	DECLARE num int;
	if (New.tipo = 'Ban') THEN
		set num =(select bans from usuarios where usuario=New.usuario);
		if (num >= 2) then
			UPDATE usuarios SET bans=0 WHERE usuario=New.usuario;
			set New.caduca=NULL;
		else
			UPDATE usuarios SET bans=bans+1 WHERE usuario=New.usuario;
		end if;
	ELSE
		set num = (select kicks from usuarios where usuario=New.usuario);
		if(num >= 2) then
			UPDATE usuarios SET kicks=0 WHERE usuario=New.usuario;
			UPDATE usuarios SET bans=bans+1 WHERE usuario=New.usuario;
			set New.tipo='Ban';
			set New.caduca=ADDTIME(CURRENT_TIMESTAMP, '1 0:0:0.000000');
			set num =(select bans from usuarios where usuario=New.usuario);
			if (num >= 2) then
				UPDATE usuarios SET bans=0 WHERE usuario=New.usuario;
				set New.caduca=NULL;
			end if;
		else
			UPDATE usuarios SET kicks=kicks+1 WHERE usuario=New.usuario;
		end if;
	END IF;

END